package com.forvity.app.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "app.bootstrap.admin.email=root@example.com",
        "app.bootstrap.admin.password=secret123"
})
class SystemRoleControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldListSystemRolesWhenAuthenticatedAsRoot() throws Exception {
        final var session = loginAsRoot();
        createSuperAdmin(session, "super@example.com");

        mockMvc.perform(get("/api/v1/system/roles").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].role").isNotEmpty())
                .andExpect(jsonPath("$[0].email").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedOnListWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/system/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreateSuperAdminWhenAuthenticatedAsRoot() throws Exception {
        final var session = loginAsRoot();

        mockMvc.perform(post("/api/v1/system/roles")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "super@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("super@example.com"))
                .andExpect(jsonPath("$.role").value("SUPERADMIN"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/system/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "super@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnBadRequestWhenEmailAlreadyInUse() throws Exception {
        final var session = loginAsRoot();

        mockMvc.perform(post("/api/v1/system/roles")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "root@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRevokeSuperAdminWhenAuthenticatedAsRoot() throws Exception {
        final var session = loginAsRoot();
        createSuperAdmin(session, "super@example.com");
        final var roleId = createSuperAdmin(session, "super2@example.com");

        mockMvc.perform(delete("/api/v1/system/roles/{id}", roleId)
                        .session(session))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenRevokingNonExistentRole() throws Exception {
        final var session = loginAsRoot();

        mockMvc.perform(delete("/api/v1/system/roles/{id}", UUID.randomUUID())
                        .session(session))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenRevokingLastSuperAdmin() throws Exception {
        final var session = loginAsRoot();
        final var roleId = createSuperAdmin(session, "super@example.com");

        // Log in as the new SUPERADMIN and try to revoke themselves — blocked as last SUPERADMIN
        // Instead: create two, revoke first, then try to revoke second
        final var secondRoleId = createSuperAdmin(session, "super2@example.com");

        mockMvc.perform(delete("/api/v1/system/roles/{id}", roleId)
                        .session(session))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/v1/system/roles/{id}", secondRoleId)
                        .session(session))
                .andExpect(status().isBadRequest());
    }

    private UUID createSuperAdmin(final MockHttpSession session, final String email) throws Exception {
        final var result = mockMvc.perform(post("/api/v1/system/roles")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "secret123"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andReturn();

        final var response = objectMapper.readTree(result.getResponse().getContentAsString());
        return UUID.fromString(response.get("id").asText());
    }

    private MockHttpSession loginAsRoot() throws Exception {
        final var result = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "root@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession();
    }
}
