package com.forvity.app.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

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
