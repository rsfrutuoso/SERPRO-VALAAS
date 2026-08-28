package com.valaas.validator.application.port.in;

import jakarta.validation.constraints.NotBlank;

public record CreateValidationCommand(
        @NotBlank(message = "tenantId is required") String tenantId,
        @NotBlank(message = "profileId is required") String profileId,
        @NotBlank(message = "payload is required") String payload,
        @NotBlank(message = "idempotencyKey is required") String idempotencyKey
) {
}
