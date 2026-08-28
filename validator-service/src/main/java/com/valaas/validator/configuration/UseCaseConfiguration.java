package com.valaas.validator.configuration;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.application.usecase.CreateValidationUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public CreateValidationUseCase createValidationUseCase(ValidationRequestRepository repository) {
        return new CreateValidationUseCase(repository);
    }
}
