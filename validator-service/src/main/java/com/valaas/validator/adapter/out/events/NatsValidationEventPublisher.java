package com.valaas.validator.adapter.out.events;

import com.valaas.validator.application.port.out.ValidationEventPublisher;
import com.valaas.validator.domain.model.ValidationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "valaas.messaging.enabled", havingValue = "true", matchIfMissing = false)
public class NatsValidationEventPublisher implements ValidationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NatsValidationEventPublisher.class);

    @Override
    public void publishValidationCreated(ValidationRequest request) {
        log.info("Publishing validation.created event for requestId={} tenantId={} profileId={}",
                request.getId(), request.getTenantId(), request.getProfileId());
    }
}
