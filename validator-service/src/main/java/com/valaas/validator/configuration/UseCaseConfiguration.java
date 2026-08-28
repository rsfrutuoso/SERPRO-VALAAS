package com.valaas.validator.configuration;

import com.valaas.validator.application.port.out.ValidationEventPublisher;
import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.application.usecase.CreateValidationUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class UseCaseConfiguration {

    @Bean
    @Primary
    public CreateValidationUseCase createValidationUseCase(ValidationRequestRepository repository) {
        return new CreateValidationUseCase(repository);
    }

    @Bean(name = "createValidationUseCaseWithEventPublisher")
    @ConditionalOnBean(ValidationEventPublisher.class)
    public CreateValidationUseCase createValidationUseCaseWithEventPublisher(ValidationRequestRepository repository,
                                                                             ValidationEventPublisher eventPublisher) {
        return new CreateValidationUseCase(repository, eventPublisher);
    }
}
