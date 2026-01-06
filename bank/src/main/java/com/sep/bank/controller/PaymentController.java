package com.sep.bank.controller;

import com.sep.bank.dto.GetRedirectUrlRequest;
import com.sep.bank.dto.GetRedirectUrlResponse;
import com.sep.bank.dto.PayRequest;
import com.sep.bank.dto.PayResponse;
import com.sep.bank.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/url")
    public ResponseEntity<GetRedirectUrlResponse> generateRedirectUrl(@RequestBody GetRedirectUrlRequest request) {
        return ResponseEntity.ok(paymentService.generateRedirectUrl(request));
    }

    @PostMapping("/pay/{paymentId}")
    public ResponseEntity<PayResponse> pay(@PathVariable UUID paymentId, @RequestBody PayRequest request) {
        return ResponseEntity.ok(paymentService.pay(paymentId, request));
    }
}
