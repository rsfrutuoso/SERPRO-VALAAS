package com.valaas.validator.configuration;

import io.nats.client.Connection;
import io.nats.client.Nats;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NatsConfiguration {

    @Bean
    @ConditionalOnProperty(name = "valaas.messaging.enabled", havingValue = "true")
    public Connection natsConnection(@Value("${nats.url:nats://localhost:4222}") String natsUrl) throws Exception {
        return Nats.connect(natsUrl);
    }
}
