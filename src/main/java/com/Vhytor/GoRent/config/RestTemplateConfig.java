package com.Vhytor.GoRent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client configuration.
 * RestTemplate is declared as a Bean so Spring manages
 * its lifecycle and it can be injected anywhere it's needed.
 * This avoids creating a new instance on every request.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
