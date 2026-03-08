package com.forvity.app.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                .with(user("admin").roles("SUPERADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "FC Stockholm",
                          "slug": "fc-stockholm"
                        }
                        """));
    }

    @Test
    void shouldReturnCreatedWhenValidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "username": "john",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.email").value("john@example.com"))
            .andExpect(jsonPath("$.username").value("john"))
            .andExpect(jsonPath("$.roles[0]").value("MEMBER"));
    }

    @Test
    void shouldReturnNotFoundWhenClubDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/unknown-club/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "username": "john",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenEmailAlreadyInUse() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "username": "john",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "username": "john2",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Email already in use"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "username": "john",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.email").isNotEmpty());
    }
}
