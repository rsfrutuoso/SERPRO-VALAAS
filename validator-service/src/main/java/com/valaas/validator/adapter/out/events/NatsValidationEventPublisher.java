package com.valaas.validator.adapter.out.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.valaas.validator.application.port.out.ValidationEventPublisher;
import com.valaas.validator.domain.model.ValidationRequest;
import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Component
@ConditionalOnProperty(name = "valaas.messaging.enabled", havingValue = "true", matchIfMissing = false)
public class NatsValidationEventPublisher implements ValidationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NatsValidationEventPublisher.class);

    private final Connection natsConnection;
    private final ObjectMapper objectMapper;

    public NatsValidationEventPublisher(Connection natsConnection, ObjectMapper objectMapper) {
        this.natsConnection = natsConnection;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publishValidationCreated(ValidationRequest request) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "event", "validation.created",
                    "validationId", request.getId().toString(),
                    "tenantId", request.getTenantId(),
                    "profileId", request.getProfileId(),
                    "status", request.getStatus()
            ));
            natsConnection.publish("validation.created", payload.getBytes());
            natsConnection.flush(Duration.ofSeconds(5));
            log.info("Published validation.created event for requestId={} tenantId={} profileId={}",
                    request.getId(), request.getTenantId(), request.getProfileId());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to serialize validation event payload", e);
        } catch (TimeoutException e) {
            throw new IllegalStateException("Timed out while flushing validation event to NATS", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while flushing validation event to NATS", e);
        }
    }
}
