package com.valaas.validator.adapter.out.events;

import com.valaas.validator.application.port.out.ValidationEventPublisher;
import com.valaas.validator.domain.model.ValidationRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(ValidationEventPublisher.class)
public class NoopValidationEventPublisher implements ValidationEventPublisher {

    @Override
    public void publishValidationCreated(ValidationRequest request) {
        // intentionally no-op when messaging is disabled or not configured
    }
}
