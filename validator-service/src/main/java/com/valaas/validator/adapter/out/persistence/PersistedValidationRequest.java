package com.valaas.validator.adapter.out.persistence;

import com.valaas.validator.domain.model.ValidationRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "validation_requests")
public class PersistedValidationRequest {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "profile_id", nullable = false)
    private String profileId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    protected PersistedValidationRequest() {
    }

    private PersistedValidationRequest(UUID id, String tenantId, String profileId, String payload, String status, String idempotencyKey) {
        this.id = id;
        this.tenantId = tenantId;
        this.profileId = profileId;
        this.payload = payload;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
    }

    public static PersistedValidationRequest fromDomain(ValidationRequest request) {
        return new PersistedValidationRequest(
                request.getId(),
                request.getTenantId(),
                request.getProfileId(),
                request.getPayload(),
                request.getStatus(),
                request.getIdempotencyKey()
        );
    }

    public ValidationRequest toDomain() {
        return ValidationRequest.builder()
                .id(this.id)
                .tenantId(this.tenantId)
                .profileId(this.profileId)
                .payload(this.payload)
                .status(this.status)
                .idempotencyKey(this.idempotencyKey)
                .build();
    }

    public UUID getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getPayload() {
        return payload;
    }

    public String getStatus() {
        return status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }
}
