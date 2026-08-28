package com.valaas.validator.application.usecase;

import com.valaas.validator.application.port.in.CreateValidationCommand;
import com.valaas.validator.domain.model.ValidationRequest;
import com.valaas.validator.application.port.out.ValidationRequestRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateValidationUseCaseTest {

    @Test
    void shouldCreateValidationRequestWithGeneratedId() {
        ValidationRequestRepository repository = mock(ValidationRequestRepository.class);
        ValidationRequest stored = ValidationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .build();

        when(repository.save(any(ValidationRequest.class))).thenReturn(stored);

        CreateValidationUseCase useCase = new CreateValidationUseCase(repository);
        CreateValidationCommand command = new CreateValidationCommand("tenant-001", "profile-001", "{\"documentNumber\":\"12345678900\"}", "idempotency-key-1");

        ValidationRequest result = useCase.execute(command);

        assertThat(result).isNotNull();
        assertThat(result.getTenantId()).isEqualTo("tenant-001");
        assertThat(result.getProfileId()).isEqualTo("profile-001");
        assertThat(result.getStatus()).isEqualTo("RECEIVED");
    }

    @Test
    void shouldRejectMissingTenant() {
        ValidationRequestRepository repository = mock(ValidationRequestRepository.class);
        CreateValidationUseCase useCase = new CreateValidationUseCase(repository);
        CreateValidationCommand command = new CreateValidationCommand(null, "profile-001", "{\"documentNumber\":\"12345678900\"}", "idempotency-key-2");

        assertThatThrownBy(() -> useCase.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenantId");
    }
}
