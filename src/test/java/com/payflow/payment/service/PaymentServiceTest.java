package com.payflow.payment.service;

import com.payflow.payment.dto.CreatePaymentRequest;
import com.payflow.payment.dto.PaymentResponse;
import com.payflow.payment.entity.Payment;
import com.payflow.payment.entity.PaymentStatus;
import com.payflow.payment.exception.PaymentStateException;
import com.payflow.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository);
    }

    @Test
    void shouldCreatePaymentSuccessfully() {

        CreatePaymentRequest request = new CreatePaymentRequest(
                "ACC-1001",
                "ACC-2001",
                new BigDecimal("125.50"),
                "usd"
        );

        when(paymentRepository.save(any(Payment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.createPayment(request);

        assertNotNull(response.id());
        assertNotNull(response.paymentReference());
        assertEquals("ACC-1001", response.sourceAccount());
        assertEquals("ACC-2001", response.destinationAccount());
        assertEquals(new BigDecimal("125.50"), response.amount());
        assertEquals("USD", response.currency());
        assertEquals(PaymentStatus.PENDING, response.status());

        verify(paymentRepository, times(1))
                .save(any(Payment.class));
    }

    @Test
    void shouldCancelPendingPayment() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setPaymentReference("PAY-123");
        payment.setSourceAccount("ACC-1001");
        payment.setDestinationAccount("ACC-2001");
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency("USD");
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.cancelPayment(paymentId);

        assertEquals(PaymentStatus.CANCELLED, response.status());
        assertEquals(PaymentStatus.CANCELLED, payment.getStatus());

        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void shouldRejectCancellationForCompletedPayment() {

        UUID paymentId = UUID.randomUUID();

        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.COMPLETED);

        when(paymentRepository.findById(paymentId))
                .thenReturn(Optional.of(payment));

        PaymentStateException exception = assertThrows(
                PaymentStateException.class,
                () -> paymentService.cancelPayment(paymentId)
        );

        assertEquals(
                "Completed payments cannot be cancelled",
                exception.getMessage()
        );

        verify(paymentRepository, times(1)).findById(paymentId);
    }
}