package org.example.taller7.service;

import org.example.taller7.dto.CashRequestDto;
import org.example.taller7.dto.TransactionDto;
import org.example.taller7.handler.Publisher;
import org.example.taller7.model.Transaction;
import org.example.taller7.model.TransactionStatus;
import org.example.taller7.model.TransactionType;
import org.example.taller7.repository.TransactionRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Instant;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final LedgerRequestReplyClient ledgerRequestReplyClient;

    public TransactionService(TransactionRepository transactionRepository,
                              LedgerRequestReplyClient ledgerRequestReplyClient,
                              Publisher publisher) {
        this.transactionRepository = transactionRepository;
        this.ledgerRequestReplyClient = ledgerRequestReplyClient;
    }

public Mono<TransactionDto> cashIn(CashRequestDto cashRequestDto) {
    Transaction tx = Transaction.builder()
            .amount(cashRequestDto.amount())
            .currency(cashRequestDto.currency())
            .type(TransactionType.CASH_IN)
            .status(TransactionStatus.PENDING)
            .createdAt(Instant.now())
            .build();

    return Mono.just(tx)
            .flatMap(ledgerRequestReplyClient::sendTransaction)
            .flatMap(transactionRepository::save)
            .map(this::toDto)
            .onErrorResume(e-> rollback(tx,e));

    }

    public Mono<TransactionDto> cashOut(CashRequestDto cashRequestDto) {
        Transaction tx = Transaction.builder()
                .amount(cashRequestDto.amount())
                .currency(cashRequestDto.currency())
                .type(TransactionType.CASH_OUT)
                .status(TransactionStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        return Mono.just(tx)
                .flatMap(ledgerRequestReplyClient::sendTransaction)
                .flatMap(transactionRepository::save)
               .map(this::toDto);
    }

    public Mono<TransactionDto> findById(String id) {
        return transactionRepository.findById(id)
                .map(this::toDto)
                .switchIfEmpty(Mono.error(new ChangeSetPersister.NotFoundException()));
    }

    private Mono<TransactionDto> rollback(Transaction tx, Throwable e) {
        tx.setStatus(TransactionStatus.FAILED);
        return transactionRepository.save(tx)
                .then(Mono.error(e));
    }

    private TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }


}