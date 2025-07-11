package org.example.taller7.handler;

import org.example.taller7.dto.TransactionDto;
import org.example.taller7.model.Transaction;
import org.example.taller7.model.TransactionStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LedgerConsumer {

    public static final String QUEUE_NAME = "study.taller7.queue";

    @RabbitListener(queues = QUEUE_NAME)
    public Transaction receive(Transaction tx) {
        tx.setStatus(TransactionStatus.POSTED );
        tx.setCreatedAt(Instant.now());
        return tx;
    }
}
