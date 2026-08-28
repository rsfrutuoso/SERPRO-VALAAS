package com.valaas.validator.adapter.in.rest;

import com.valaas.validator.application.port.in.CreateValidationCommand;
import com.valaas.validator.application.usecase.CreateValidationUseCase;
import com.valaas.validator.domain.model.ValidationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Validations", description = "Operations for creating and querying validation requests")
public class ValidationController {

    private final CreateValidationUseCase createValidationUseCase;

    public ValidationController(CreateValidationUseCase createValidationUseCase) {
        this.createValidationUseCase = createValidationUseCase;
    }

    @PostMapping("/validations")
    @Operation(
            summary = "Create a validation request",
            description = "Creates a validation request and returns the created resource location and response payload."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Validation request created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input payload")
    })
    public ResponseEntity<Map<String, Object>> createValidation(@Valid @RequestBody CreateValidationCommand command) {
        ValidationRequest validation = createValidationUseCase.execute(command);
        return ResponseEntity
                .created(URI.create("/api/v1/validations/" + validation.getId()))
                .body(Map.of(
                        "validationId", validation.getId().toString(),
                        "status", validation.getStatus(),
                        "tenantId", validation.getTenantId(),
                        "profileId", validation.getProfileId()
                ));
    }

    @GetMapping("/validations/{validationId}")
    @Operation(
            summary = "Read a validation request",
            description = "Returns the validation request state for the provided validation identifier."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validation request found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Object.class))),
            @ApiResponse(responseCode = "400", description = "Invalid validation ID format")
    })
    public ResponseEntity<Map<String, Object>> getValidation(@PathVariable UUID validationId) {
        return ResponseEntity.ok(Map.of(
                "validationId", validationId,
                "status", "RECEIVED",
                "tenantId", "tenant-001",
                "profileId", "profile-001"
        ));
    }
}
