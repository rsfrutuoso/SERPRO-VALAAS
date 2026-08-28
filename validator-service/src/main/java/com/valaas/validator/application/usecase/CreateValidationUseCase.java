package com.valaas.validator.application.usecase;

import com.valaas.validator.application.port.in.CreateValidationCommand;
import com.valaas.validator.application.port.out.ValidationEventPublisher;
import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;

import java.util.UUID;

public class CreateValidationUseCase {

    private final ValidationRequestRepository repository;
    private final ValidationEventPublisher eventPublisher;

    public CreateValidationUseCase(ValidationRequestRepository repository) {
        this(repository, request -> {
        });
    }

    public CreateValidationUseCase(ValidationRequestRepository repository, ValidationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public ValidationRequest execute(CreateValidationCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command is required");
        }

        String tenantId = requireText(command.tenantId(), "tenantId");
        String profileId = requireText(command.profileId(), "profileId");
        String payload = requireText(command.payload(), "payload");
        String idempotencyKey = requireText(command.idempotencyKey(), "idempotencyKey");

        ValidationRequest request = ValidationRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .profileId(profileId)
                .payload(payload)
                .status("RECEIVED")
                .idempotencyKey(idempotencyKey)
                .build();

        ValidationRequest saved = repository.save(request);
        eventPublisher.publishValidationCreated(saved);
        return saved;
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }
}
