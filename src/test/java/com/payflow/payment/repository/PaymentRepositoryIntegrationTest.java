package com.payflow.payment.repository;

import com.payflow.payment.entity.Payment;
import com.payflow.payment.entity.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@ImportAutoConfiguration(FlywayAutoConfiguration.class)
class PaymentRepositoryIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16");

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void shouldSaveAndRetrievePayment() {

        System.out.println(postgres.getJdbcUrl());
        System.out.println(postgres.getUsername());
        System.out.println(postgres.getPassword());

        UUID paymentId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentReference("PAY-TEST-001");
        payment.setSourceAccount("ACC-1001");
        payment.setDestinationAccount("ACC-2001");
        payment.setAmount(new BigDecimal("125.50"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.PENDING);

        Instant now = Instant.now();
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);

        paymentRepository.save(payment);

        Optional<Payment> result =
                paymentRepository.findById(paymentId);

        assertTrue(result.isPresent());

        Payment savedPayment = result.get();

        assertEquals(
                "PAY-TEST-001",
                savedPayment.getPaymentReference()
        );

        assertEquals(
                PaymentStatus.PENDING,
                savedPayment.getStatus()
        );

        assertEquals(
                new BigDecimal("125.50"),
                savedPayment.getAmount()
        );
    }
}