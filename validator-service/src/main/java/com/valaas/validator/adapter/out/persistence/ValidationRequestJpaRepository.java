package com.valaas.validator.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ValidationRequestJpaRepository extends JpaRepository<PersistedValidationRequest, UUID> {
}
