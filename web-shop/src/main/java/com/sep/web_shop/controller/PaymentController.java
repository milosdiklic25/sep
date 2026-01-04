package com.sep.web_shop.controller;

import com.sep.web_shop.dto.CreatePaymentRequest;
import com.sep.web_shop.dto.CreatePaymentResponse;
import com.sep.web_shop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CreatePaymentResponse> create(@RequestBody CreatePaymentRequest req) {
        return ResponseEntity.ok(paymentService.createPayment(req));
    }
}