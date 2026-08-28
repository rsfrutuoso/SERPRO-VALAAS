package com.valaas.validator.adapter.out.persistence;

import com.valaas.validator.domain.model.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryValidationRequestRepositoryTest {

    @Test
    void shouldSaveValidationRequestAndReturnIt() {
        ValidationRequestJpaRepository repository = mock(ValidationRequestJpaRepository.class);
        ValidationRequest request = ValidationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idem-key-1")
                .build();

        PersistedValidationRequest persisted = PersistedValidationRequest.fromDomain(request);
        when(repository.save(any(PersistedValidationRequest.class))).thenReturn(persisted);

        ValidationRequest saved = new ValidationRequestJpaRepositoryAdapter(repository).save(request);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(request.getId());
        assertThat(saved.getTenantId()).isEqualTo("tenant-001");
        assertThat(saved.getStatus()).isEqualTo("RECEIVED");
    }
}
