package com.valaas.validator.adapter.in.rest;

import com.valaas.validator.application.port.in.CreateValidationCommand;
import com.valaas.validator.application.usecase.CreateValidationUseCase;
import com.valaas.validator.domain.model.ValidationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationControllerTest {

    @Mock
    private CreateValidationUseCase createValidationUseCase;

    @InjectMocks
    private ValidationController controller;

    @Test
    void shouldCreateValidationAndReturnCreatedResponse() {
        UUID id = UUID.randomUUID();
        ValidationRequest request = ValidationRequest.builder()
                .id(id)
                .tenantId("tenant-001")
                .profileId("profile-001")
                .payload("{\"documentNumber\":\"12345678900\"}")
                .status("RECEIVED")
                .idempotencyKey("idem-key-1")
                .build();

        when(createValidationUseCase.execute(any(CreateValidationCommand.class))).thenReturn(request);

        CreateValidationCommand command = new CreateValidationCommand(
                "tenant-001",
                "profile-001",
                "{\"documentNumber\":\"12345678900\"}",
                "idem-key-1"
        );

        ResponseEntity<Map<String, Object>> response = controller.createValidation(command);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("/api/v1/validations/" + id));
        assertThat(response.getBody())
                .containsEntry("validationId", id.toString())
                .containsEntry("status", "RECEIVED")
                .containsEntry("tenantId", "tenant-001")
                .containsEntry("profileId", "profile-001");
    }

    @Test
    void shouldReturnValidationDetailsById() {
        UUID validationId = UUID.randomUUID();

        ResponseEntity<Map<String, Object>> response = controller.getValidation(validationId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .containsEntry("validationId", validationId)
                .containsEntry("status", "RECEIVED")
                .containsEntry("tenantId", "tenant-001")
                .containsEntry("profileId", "profile-001");
    }
}
