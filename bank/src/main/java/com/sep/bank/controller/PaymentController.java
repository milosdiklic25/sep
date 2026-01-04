package com.sep.bank.controller;

import com.sep.bank.dto.GetRedirectUrlRequest;
import com.sep.bank.dto.GetRedirectUrlResponse;
import com.sep.bank.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/url")
    public ResponseEntity<GetRedirectUrlResponse> generateRedirectUrl(@RequestBody GetRedirectUrlRequest request) {
        return ResponseEntity.ok(paymentService.generateRedirectUrl(request));
    }
}
