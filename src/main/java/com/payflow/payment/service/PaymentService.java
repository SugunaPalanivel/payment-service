package com.payflow.payment.service;

import com.payflow.payment.dto.CreatePaymentRequest;
import com.payflow.payment.dto.PaymentResponse;
import com.payflow.payment.entity.Payment;
import com.payflow.payment.entity.PaymentStatus;
import com.payflow.payment.exception.PaymentNotFoundException;
import com.payflow.payment.exception.PaymentStateException;
import com.payflow.payment.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Payment payment = new Payment();

        payment.setId(UUID.randomUUID());
        payment.setPaymentReference(generatePaymentReference());
        payment.setSourceAccount(request.sourceAccount());
        payment.setDestinationAccount(request.destinationAccount());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency().toUpperCase());
        payment.setStatus(PaymentStatus.PENDING);

        Instant now = Instant.now();
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID();
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentReference(),
                payment.getSourceAccount(),
                payment.getDestinationAccount(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getPayments(Pageable pageable) {

        return paymentRepository
                .findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public PaymentResponse cancelPayment(UUID id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new PaymentStateException(
                    "Completed payments cannot be cancelled"
            );
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            return toResponse(payment);
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setUpdatedAt(Instant.now());

        return toResponse(payment);
    }
}