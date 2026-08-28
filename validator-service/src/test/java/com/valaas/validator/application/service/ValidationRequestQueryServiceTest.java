package com.valaas.validator.application.service;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ValidationRequestQueryServiceTest {

    @Test
    void shouldCacheValidationRequestById() {
        ValidationRequestRepository repository = mock(ValidationRequestRepository.class);
        UUID id = UUID.randomUUID();
        ValidationRequest request = ValidationRequest.builder()
                .id(id)
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idempotency-key-1")
                .build();
        when(repository.findById(id)).thenReturn(Optional.of(request));

        ValidationRequestQueryService service = new ValidationRequestQueryService(repository);

        ValidationRequest first = service.findById(id);
        ValidationRequest second = service.findById(id);

        assertThat(first).isEqualTo(request);
        assertThat(second).isEqualTo(request);
        verify(repository, times(1)).findById(id);
    }
}
