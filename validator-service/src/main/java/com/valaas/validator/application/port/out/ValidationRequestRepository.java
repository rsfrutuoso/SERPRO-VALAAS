package com.valaas.validator.application.port.out;

import com.valaas.validator.domain.model.ValidationRequest;

public interface ValidationRequestRepository {
    ValidationRequest save(ValidationRequest request);
}
