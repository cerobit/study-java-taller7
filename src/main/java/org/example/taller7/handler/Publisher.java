package org.example.taller7.handler;

import org.example.taller7.dto.TransactionDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

import static org.example.taller7.config.AMQPConfig.EXCHANGE_NAME;
import static org.example.taller7.config.AMQPConfig.ROUTING_KEY;

@Component
public class Publisher {
   private final AmqpTemplate amqpTemplate;

    public Publisher(AmqpTemplate amqpTemplate) {
         this.amqpTemplate = amqpTemplate;
    }

    public void publish(TransactionDto transaction) {
        amqpTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, transaction);
    }
}
