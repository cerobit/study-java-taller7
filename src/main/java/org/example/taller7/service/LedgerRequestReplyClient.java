package org.example.taller7.service;

import org.example.taller7.model.Transaction;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class LedgerRequestReplyClient {

    public static final String EXCHANGE_NAME = "study.taller7.exchange";
    public static final String ROUTING_KEY = "study.taller7.routing.key";

    private final RabbitTemplate rabbitTemplate;

    public LedgerRequestReplyClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<Transaction> sendTransaction(Transaction tx) {
        return Mono.fromCallable(() ->
                (Transaction) rabbitTemplate.convertSendAndReceive(
                        EXCHANGE_NAME ,
                        ROUTING_KEY,
                        tx
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }
}


