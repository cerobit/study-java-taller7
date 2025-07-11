package org.example.taller7.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@Document
public class Transaction {
    // campos: id, amount, currency, type, status, createdAt
    @Id
    private String id;
    @Positive
    private BigDecimal amount;
    @NotBlank
    private String currency;
    private TransactionType type;
    private TransactionStatus status;
    private Instant createdAt;
}