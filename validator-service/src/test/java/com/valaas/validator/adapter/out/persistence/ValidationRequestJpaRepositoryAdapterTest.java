package com.valaas.validator.adapter.out.persistence;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ValidationRequestJpaRepositoryAdapter.class)
@ActiveProfiles("test")
class ValidationRequestJpaRepositoryAdapterTest {

    @Autowired
    private ValidationRequestRepository repository;

    @Test
    void shouldPersistValidationRequest() {
        ValidationRequest request = ValidationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idempotency-key-1")
                .build();

        ValidationRequest saved = repository.save(request);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTenantId()).isEqualTo("tenant-001");
        assertThat(saved.getStatus()).isEqualTo("RECEIVED");
    }
}
