package com.valaas.validator.adapter.out.persistence;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.domain.model.ValidationRequest;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryValidationRequestRepository implements ValidationRequestRepository {

    private final Map<String, ValidationRequest> storage = new ConcurrentHashMap<>();

    @Override
    public ValidationRequest save(ValidationRequest request) {
        storage.put(request.getId().toString(), request);
        return request;
    }
}
