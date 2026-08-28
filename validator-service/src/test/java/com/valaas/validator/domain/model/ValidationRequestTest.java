package com.valaas.validator.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationRequestTest {

    @Test
    void shouldBuildRequestWithValuesAndAllowMutation() {
        UUID id = UUID.randomUUID();

        ValidationRequest request = ValidationRequest.builder()
                .id(id)
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idem-key-1")
                .build();

        assertThat(request.getId()).isEqualTo(id);
        assertThat(request.getTenantId()).isEqualTo("tenant-001");
        assertThat(request.getProfileId()).isEqualTo("profile-001");
        assertThat(request.getPayload()).isEqualTo("{\"documentNumber\":\"12345678900\"}");
        assertThat(request.getStatus()).isEqualTo("RECEIVED");
        assertThat(request.getIdempotencyKey()).isEqualTo("idem-key-1");

        UUID updatedId = UUID.randomUUID();
        request.setId(updatedId);
        request.setTenantId("tenant-002");
        request.setProfileId("profile-002");
        request.setPayload("{\"documentNumber\":\"98765432100\"}");
        request.setStatus("PENDING");
        request.setIdempotencyKey("idem-key-2");

        assertThat(request.getId()).isEqualTo(updatedId);
        assertThat(request.getTenantId()).isEqualTo("tenant-002");
        assertThat(request.getProfileId()).isEqualTo("profile-002");
        assertThat(request.getPayload()).isEqualTo("{\"documentNumber\":\"98765432100\"}");
        assertThat(request.getStatus()).isEqualTo("PENDING");
        assertThat(request.getIdempotencyKey()).isEqualTo("idem-key-2");
    }
}
