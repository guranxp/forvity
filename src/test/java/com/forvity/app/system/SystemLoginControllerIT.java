package com.forvity.app.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "app.bootstrap.admin.email=root@example.com",
        "app.bootstrap.admin.password=secret123"
})
class SystemLoginControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnOkWhenValidCredentials() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "root@example.com",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenWrongPassword() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "root@example.com",
                                  "password": "wrongpassword"
                                }
                                """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenUnknownEmail() throws Exception {
        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "unknown@example.com",
                                  "password": "secret123"
                                }
                                """))
            .andExpect(status().isUnauthorized());
    }
}