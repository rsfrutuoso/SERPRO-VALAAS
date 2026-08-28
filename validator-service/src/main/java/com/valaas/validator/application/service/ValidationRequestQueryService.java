package com.valaas.validator.application.service;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ValidationRequestQueryService {

    private final ValidationRequestRepository repository;
    private final Map<UUID, ValidationRequest> cache = new ConcurrentHashMap<>();

    public ValidationRequestQueryService(ValidationRequestRepository repository) {
        this.repository = repository;
    }

    public ValidationRequest findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }

        ValidationRequest cached = cache.get(id);
        if (cached != null) {
            return cached;
        }

        ValidationRequest loaded = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("validation request not found"));
        cache.put(id, loaded);
        return loaded;
    }
}
