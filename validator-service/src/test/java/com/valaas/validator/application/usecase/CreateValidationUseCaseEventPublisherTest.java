package com.valaas.validator.application.usecase;

import com.valaas.validator.application.port.in.CreateValidationCommand;
import com.valaas.validator.application.port.out.ValidationEventPublisher;
import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateValidationUseCaseEventPublisherTest {

    @Test
    void shouldPublishValidationCreatedEventAfterSaving() {
        ValidationRequestRepository repository = mock(ValidationRequestRepository.class);
        ValidationEventPublisher eventPublisher = mock(ValidationEventPublisher.class);
        ValidationRequest stored = ValidationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idempotency-key-1")
                .build();
        when(repository.save(any(ValidationRequest.class))).thenReturn(stored);

        CreateValidationUseCase useCase = new CreateValidationUseCase(repository, eventPublisher);
        CreateValidationCommand command = new CreateValidationCommand(
                "tenant-001",
                "profile-001",
                "{\"documentNumber\":\"12345678900\"}",
                "idempotency-key-1"
        );

        useCase.execute(command);

        verify(eventPublisher).publishValidationCreated(stored);
    }
}
