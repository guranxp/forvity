package com.forvity.app.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClubLoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                .with(user("admin").roles("SUPERADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "name": "FC Stockholm", "slug": "fc-stockholm" }
                        """));

        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "john@example.com",
                          "username": "john",
                          "password": "secret123"
                        }
                        """));
    }

    @Test
    void shouldReturnOkWhenValidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenWrongPassword() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/fc-stockholm/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "password": "wrongpassword"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnNotFoundWhenClubDoesNotExist() throws Exception {
        mockMvc.perform(post("/api/v1/clubs/unknown/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "john@example.com",
                                  "password": "secret123"
                                }
                                """))
                .andExpect(status().isNotFound());
    }
}