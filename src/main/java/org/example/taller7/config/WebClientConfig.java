package org.example.taller7.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient ledgerWebClient(WebClient.Builder builder, @Value("${ledger.base-url}") String baseUrl) {
        return builder.baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
