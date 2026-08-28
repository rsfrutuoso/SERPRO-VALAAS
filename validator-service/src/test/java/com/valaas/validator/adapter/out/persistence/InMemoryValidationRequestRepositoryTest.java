package com.valaas.validator.adapter.out.persistence;

import com.valaas.validator.domain.model.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryValidationRequestRepositoryTest {

    @Test
    void shouldSaveValidationRequestAndReturnIt() {
        InMemoryValidationRequestRepository repository = new InMemoryValidationRequestRepository();
        UUID id = UUID.randomUUID();
        ValidationRequest request = ValidationRequest.builder()
                .id(id)
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idem-key-1")
                .build();

        ValidationRequest saved = repository.save(request);

        assertThat(saved).isSameAs(request);
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getTenantId()).isEqualTo("tenant-001");
        assertThat(saved.getStatus()).isEqualTo("RECEIVED");
    }
}
