package com.forvity.app.club;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ClubControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void shouldReturnCreatedWhenValidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "FC Stockholm",
                                  "slug": "fc-stockholm"
                                }
                                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value("FC Stockholm"))
            .andExpect(jsonPath("$.slug").value("fc-stockholm"));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void shouldReturnBadRequestWhenSlugAlreadyInUse() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "FC Stockholm",
                                  "slug": "fc-stockholm"
                                }
                                """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "FC Stockholm 2",
                                  "slug": "fc-stockholm"
                                }
                                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Slug already in use"));
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void shouldReturnBadRequestWhenSlugHasInvalidFormat() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "FC Stockholm",
                                  "slug": "FC Stockholm!"
                                }
                                """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.slug").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "FC Stockholm",
                                  "slug": "fc-stockholm"
                                }
                                """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "SUPERADMIN")
    void shouldReturnBadRequestWhenFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/v1/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
