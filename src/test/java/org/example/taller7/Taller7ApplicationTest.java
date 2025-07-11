package org.example.taller7;

import org.example.taller7.dto.CashRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class Taller7ApplicationTest {

    WebTestClient client = WebTestClient.bindToServer()
            .baseUrl("http://localhost:8080")
            .build();

    @Test
    void cashInTest() {
        CashRequestDto cashRequest = new CashRequestDto(
                BigDecimal.valueOf(500),
                "USD",
                "external-id-12345"
        );

        client.post()
                .uri("/cash-in")
                .bodyValue(cashRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("POSTED");
    }

    @Test
    void cashOutTest() {
        CashRequestDto cashRequest = new CashRequestDto(
                BigDecimal.valueOf(500),
                "USD",
                "external-id-12345"
        );

        client.post()
                .uri("/cash-out")
                .bodyValue(cashRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("POSTED");
    }

    @Test
    void findByIdTest() {
        // Arrange: Create a transaction to get a valid ID
        CashRequestDto cashRequest = new CashRequestDto(
                BigDecimal.valueOf(150),
                "EUR",
                "external-id-for-find-test"
        );

        String transactionId = client.post()
                .uri("/cash-in")
                .bodyValue(cashRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Map.class)
                .returnResult()
                .getResponseBody()
                .get("id")
                .toString();

        // Act & Assert: Use the ID to test the findById endpoint
        client.get()
                .uri("/transaction/{id}", transactionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(transactionId)
                .jsonPath("$.status").isEqualTo("POSTED")
                .jsonPath("$.amount").isEqualTo(150);
    }

}