package com.valaas.validator.application.port.out;

import com.valaas.validator.domain.model.ValidationRequest;

public interface ValidationEventPublisher {
    void publishValidationCreated(ValidationRequest request);
}
