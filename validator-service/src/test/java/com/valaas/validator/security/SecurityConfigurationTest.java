package com.valaas.validator.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRejectUnauthenticatedValidationCreation() throws Exception {
        mockMvc.perform(post("/api/v1/validations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenantId\":\"tenant-001\",\"profileId\":\"profile-001\",\"payload\":\"{\\\"documentNumber\\\":\\\"12345678900\\\"}\",\"idempotencyKey\":\"idem-key-1\"}"))
                .andExpect(status().isUnauthorized());
    }
}
