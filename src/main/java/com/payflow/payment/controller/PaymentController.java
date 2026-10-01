package com.payflow.payment.controller;

import com.payflow.payment.dto.CreatePaymentRequest;
import com.payflow.payment.dto.PaymentResponse;
import com.payflow.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(
        name = "Payments",
        description = "APIs for creating, retrieving and managing payments"
)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    @Operation(
            summary = "Create a payment",
            description = "Creates a new payment with PENDING status"
    )
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @Operation(
            summary = "Get payment by ID",
            description = "Returns payment details for the supplied payment ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable UUID id) {

        PaymentResponse response = paymentService.getPayment(id);

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get payments",
            description = "Returns payments using pagination and sorting"
    )
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getPayments(Pageable pageable) {

        Page<PaymentResponse> response = paymentService.getPayments(pageable);

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Cancel a payment",
            description = "Cancels a payment if its current status allows cancellation"
    )
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID id) {

        PaymentResponse response = paymentService.cancelPayment(id);

        return ResponseEntity.ok(response);
    }
}