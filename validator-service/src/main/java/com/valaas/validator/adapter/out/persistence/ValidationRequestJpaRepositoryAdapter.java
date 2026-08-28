package com.valaas.validator.adapter.out.persistence;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ValidationRequestJpaRepositoryAdapter implements ValidationRequestRepository {

    private final ValidationRequestJpaRepository repository;

    public ValidationRequestJpaRepositoryAdapter(ValidationRequestJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public ValidationRequest save(ValidationRequest request) {
        PersistedValidationRequest persisted = PersistedValidationRequest.fromDomain(request);
        return repository.save(persisted).toDomain();
    }

    @Override
    public Optional<ValidationRequest> findById(UUID id) {
        return repository.findById(id).map(PersistedValidationRequest::toDomain);
    }
}
