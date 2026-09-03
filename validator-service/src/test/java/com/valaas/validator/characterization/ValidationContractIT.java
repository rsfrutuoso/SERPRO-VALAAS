package com.valaas.validator.characterization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valaas.validator.test.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationContractIT extends AbstractIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    ObjectMapper mapper;

    @Test
    void postValidation_shouldReturnAcceptedOrOk_and_preserveContractVariants() throws Exception {
        // Build payload using POC-style mixed field names
        String payload = "{\"sourceId\":\"src-1\",\"key_attribute\":\"cpf\",\"key_value\":\"12345678901\",\"validations\":[{\"type\":\"REGISTRATION\",\"attribute\":\"name\",\"value\":\"Joao\"}]}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Test-Client-Id", "test-client");

        HttpEntity<String> req = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = rest.postForEntity("/v1/validation", req, String.class);

        // Accept either 202 Accepted (current async path) or 200 OK (POC sync path)
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        int status = resp.getStatusCodeValue();
        assertThat(status == 200 || status == 202).isTrue();

        // If body present, ensure it follows expected structure (array of results or job id)
        if (resp.hasBody() && resp.getBody() != null && !resp.getBody().isBlank()) {
            JsonNode body = mapper.readTree(resp.getBody());
            // If POC sync style, expect validations results array
            if (body.has("results")) {
                assertThat(body.get("results").isArray()).isTrue();
            }
            // If async style, may contain jobId or location header; we accept both
        }
    }
}
