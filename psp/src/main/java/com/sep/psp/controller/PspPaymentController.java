package com.sep.psp.controller;

import com.sep.psp.dto.CardPaymentRequest;
import com.sep.psp.dto.CardPaymentResponse;
import com.sep.psp.dto.PspInitPaymentRequest;
import com.sep.psp.dto.PspInitPaymentResponse;
import com.sep.psp.service.PspPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PspPaymentController {
    @Autowired
    private PspPaymentService paymentService;

    @PostMapping("/init")
    public ResponseEntity<PspInitPaymentResponse> init(@RequestBody PspInitPaymentRequest req) {
        return ResponseEntity.ok(paymentService.initPayment(req));
    }

    @PostMapping("/card")
    public ResponseEntity<CardPaymentResponse> getUrlRedirect(@RequestBody CardPaymentRequest req) {
        return ResponseEntity.ok(paymentService.requestBankUrl(req));
    }
}
