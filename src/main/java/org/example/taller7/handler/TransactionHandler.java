package org.example.taller7.handler;

import org.example.taller7.dto.CashRequestDto;
import org.example.taller7.service.LedgerRequestReplyClient;
import org.example.taller7.service.TransactionService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
public class TransactionHandler {

    private final TransactionService trxService;


    public TransactionHandler(TransactionService transactionService, LedgerRequestReplyClient ledgerClient) {
        this.trxService = transactionService;
    }

    public Mono<ServerResponse> cashIn(ServerRequest request) {
        return  request.bodyToMono(CashRequestDto.class)
                .flatMap(trxService::cashIn)
                .flatMap(transactionDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDto));
    }

    public Mono<ServerResponse> cashOut(ServerRequest request) {
        return  request.bodyToMono(CashRequestDto.class)
                .flatMap(trxService::cashOut)
                .flatMap(transactionDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDto));
    }


    public Mono<ServerResponse> findById(ServerRequest request) {
        return  trxService.findById(request.pathVariable("id"))
                .flatMap(transactionDto -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactionDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

}