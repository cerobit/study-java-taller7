package org.example.taller7.service;

import org.example.taller7.model.Transaction;
import org.example.taller7.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
public class LedgerClient {

    private final WebClient client;
    private static final Logger log = LoggerFactory.getLogger(LedgerClient.class);

    public LedgerClient(WebClient client) {
        this.client = client;
    }

    public Mono<Transaction> postEntry(Transaction transaction) {
        log.info("Post: /ledger/entries request body {}", transaction);
        return client.post()
                .uri("/ledger/entries")
                .bodyValue(transaction)
                .retrieve()
                .bodyToMono(Transaction.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500)))
                .doOnNext(tx -> log.info("Post: /ledger/entries response body {}", tx))
                .doOnError(e -> log.error("Error posting /ledger/entries error mesage: {}", e.getMessage()))
                .map(response -> {
                    return Transaction.builder()
                            .id(transaction.getId())
                            .amount(transaction.getAmount())
                            .currency(transaction.getCurrency())
                            .type(transaction.getType())
                            .status(response.getStatus() != null ? response.getStatus() : TransactionStatus.POSTED)
                            .createdAt(transaction.getCreatedAt())
                            .build();
                });
    }
}