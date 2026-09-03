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

class SourceContractIT extends AbstractIntegrationTest {

    @Autowired
    TestRestTemplate rest;

    @Autowired
    ObjectMapper mapper;

    @Test
    void createSource_shouldNotReturnSecret() throws Exception {
        String payload = "{\"name\":\"test-source\",\"uri\":\"https://example.com/api\",\"credentials\":{\"type\":\"BASIC\",\"username\":\"u\",\"secret\":\"s\"}}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> req = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = rest.postForEntity("/v1/source", req, String.class);

        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        JsonNode body = mapper.readTree(resp.getBody());
        // Ensure the response does not contain the credential secret
        assertThat(body.at("/credentials/secret").isMissingNode() || body.at("/credentials/secret").isNull()).isTrue();
    }
}
