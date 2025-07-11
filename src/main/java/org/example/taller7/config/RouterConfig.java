package org.example.taller7.config;

import org.example.taller7.handler.TransactionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(TransactionHandler handler) {
        return RouterFunctions.route()
                .POST("/cash-in", handler::cashIn)
                .POST("/cash-out", handler::cashOut)
                .GET("/transaction/{id}", handler::findById)
                .build();
    }
}
