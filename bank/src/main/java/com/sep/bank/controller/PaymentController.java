package com.sep.bank.controller;

import com.sep.bank.dto.*;
import com.sep.bank.psp.PspUpdateStatusResponse;
import com.sep.bank.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @PostMapping("/qr")
    public ResponseEntity<GetRedirectUrlResponse> generateRedirectUrlQR(@RequestBody GetRedirectUrlRequest request) {
        return ResponseEntity.ok(paymentService.generateRedirectUrlQR(request));
    }

    @PostMapping("/pay/{paymentId}")
    public ResponseEntity<PspUpdateStatusResponse> pay(@PathVariable UUID paymentId, @RequestBody PayRequest request) {
        var resp = paymentService.pay(paymentId, request);
        var redirectUrl = paymentService.getRedirectUrl(resp);
        return ResponseEntity.ok(redirectUrl);
    }

    @PostMapping("/pay/qr/{paymentId}")
    public ResponseEntity<PspUpdateStatusResponse> payQR(@PathVariable UUID paymentId, @RequestBody PayRequest request) {
        var resp = paymentService.pay(paymentId, request);
        var redirectUrl = paymentService.getRedirectUrl(resp);
        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/amount/{paymentId}")
    public ResponseEntity<Double> getAmount(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getAmount(paymentId));
    }

    @GetMapping(value = "/qr/{paymentId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qr(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(paymentService.getQR(paymentId));
    }

    @PostMapping("/qr/validate/{paymentId}")
    public ResponseEntity<QrValidateResponse> validateQr(
            @PathVariable UUID paymentId,
            @RequestBody QrValidateRequest request
    ) {
        boolean valid = paymentService.validateQrPayload(paymentId, request.payload());
        return ResponseEntity.ok(new QrValidateResponse(valid, valid ? "OK" : "INVALID_QR"));
    }
}
