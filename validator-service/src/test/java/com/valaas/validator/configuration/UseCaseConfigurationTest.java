package com.valaas.validator.configuration;

import com.valaas.validator.application.port.out.ValidationRequestRepository;
import com.valaas.validator.application.usecase.CreateValidationUseCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UseCaseConfigurationTest {

    @Test
    void shouldCreateValidationUseCaseBean() {
        ValidationRequestRepository repository = mock(ValidationRequestRepository.class);
        UseCaseConfiguration configuration = new UseCaseConfiguration();

        CreateValidationUseCase useCase = configuration.createValidationUseCase(repository);

        assertThat(useCase).isNotNull();
        assertThat(useCase).isInstanceOf(CreateValidationUseCase.class);
    }
}
