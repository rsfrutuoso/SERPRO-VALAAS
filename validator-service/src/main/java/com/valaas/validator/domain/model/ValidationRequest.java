package com.valaas.validator.domain.model;

import java.util.UUID;

public class ValidationRequest {

    private UUID id;
    private String tenantId;
    private String profileId;
    private String payload;
    private String status;
    private String idempotencyKey;

    private ValidationRequest(Builder builder) {
        this.id = builder.id;
        this.tenantId = builder.tenantId;
        this.profileId = builder.profileId;
        this.payload = builder.payload;
        this.status = builder.status;
        this.idempotencyKey = builder.idempotencyKey;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public static class Builder {
        private UUID id;
        private String tenantId;
        private String profileId;
        private String payload;
        private String status;
        private String idempotencyKey;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder profileId(String profileId) {
            this.profileId = profileId;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public ValidationRequest build() {
            return new ValidationRequest(this);
        }
    }
}
