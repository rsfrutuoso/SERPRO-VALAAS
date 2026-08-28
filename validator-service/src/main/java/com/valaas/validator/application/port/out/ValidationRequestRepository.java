package com.valaas.validator.application.port.out;

import com.valaas.validator.domain.model.ValidationRequest;

import java.util.Optional;
import java.util.UUID;

public interface ValidationRequestRepository {
    ValidationRequest save(ValidationRequest request);
    Optional<ValidationRequest> findById(UUID id);
}
