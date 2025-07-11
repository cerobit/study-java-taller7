package org.example.taller7.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.taller7.service.LedgerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Throwable.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        objectMapper.registerModule(new JavaTimeModule());
        HttpStatus status;
        if (ex instanceof ResponseStatusException) {
            status = HttpStatus.valueOf(((ResponseStatusException) ex).getStatusCode().value());
        } else if (ex instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        Map<String, Object> error = new HashMap<>(
                Map.of(
                        "timestamp", Instant.now(),
                        "error", status.getReasonPhrase(),
                        "status", status.value(),
                        "message", ex.getMessage()
                )
        );

        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(error);
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.error("Error occurred: {}", error);

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory bufferFactory = response.bufferFactory();

        return response.writeWith(Mono.just(bufferFactory.wrap(bytes)))
                .doOnError(e -> e.printStackTrace());
    }
}