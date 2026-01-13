package com.sep.bank.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sep.bank.dto.GetRedirectUrlRequest;
import com.sep.bank.dto.GetRedirectUrlResponse;
import com.sep.bank.dto.PayRequest;
import com.sep.bank.dto.PayResponse;
import com.sep.bank.exception.MerchantNotFoundException;
import com.sep.bank.model.Payment;
import com.sep.bank.model.Payment.Status;
import com.sep.bank.psp.PspClient;
import com.sep.bank.psp.PspUpdateStatusRequest;
import com.sep.bank.psp.PspUpdateStatusResponse;
import com.sep.bank.repository.BankMerchantRepository;
import com.sep.bank.repository.CardRepository;
import com.sep.bank.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private BankMerchantRepository bankMerchantRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private PspClient pspClient;

    private final String bankFrontendBaseUrl;

    public PaymentService(@Value("${bank.frontend.base-url:http://localhost:4202}") String bankFrontendBaseUrl) {
        this.bankFrontendBaseUrl = bankFrontendBaseUrl;
    }

    @Transactional
    public GetRedirectUrlResponse generateRedirectUrl(GetRedirectUrlRequest req) {
        var merchant = bankMerchantRepository.findByMerchantId(req.bankMerchantId());
        if (merchant.isEmpty()) {
            throw new MerchantNotFoundException(req.bankMerchantId());
        }

        //UUID paymentStan = UUID.randomUUID();
        Payment payment = Payment.builder()
                .bankMerchantId(req.bankMerchantId())
                .amount(req.amount())
                .currency(req.currency())
                .stan(req.pspPaymentId())
                .pspTimestamp(req.pspTimestamp())
                .status(Status.PSP_INITIATED)
                .build();
        Payment saved = paymentRepository.save(payment);
        String url = bankFrontendBaseUrl + "/pay/" + saved.getId().toString();

        return new GetRedirectUrlResponse(url, saved.getId());
    }

    @Transactional
    public GetRedirectUrlResponse generateRedirectUrlQR(GetRedirectUrlRequest req) {
        var merchant = bankMerchantRepository.findByMerchantId(req.bankMerchantId());
        if (merchant.isEmpty()) {
            throw new MerchantNotFoundException(req.bankMerchantId());
        }

        //UUID paymentStan = UUID.randomUUID();
        Payment payment = Payment.builder()
                .bankMerchantId(req.bankMerchantId())
                .amount(req.amount())
                .currency(req.currency())
                .stan(req.pspPaymentId())
                .pspTimestamp(req.pspTimestamp())
                .status(Status.PSP_INITIATED)
                .build();
        Payment saved = paymentRepository.save(payment);
        String url = bankFrontendBaseUrl + "/pay/qr/" + saved.getId().toString();

        return new GetRedirectUrlResponse(url, saved.getId());
    }

    @Transactional
    public PayResponse pay(UUID paymentId, PayRequest req) {
        var cardOpt = cardRepository.findByCardholderNameAndCardNumberAndExpiryDateAndCvv(
                req.cardholderName(),
                req.cardNumber(),
                req.expiryDate(),
                req.cvv()
        );

        if (cardOpt.isEmpty()) {
            return PayResponse.fail("Card not found or details are invalid", paymentId, null, Status.FAILED);
        }

        var paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            return PayResponse.fail("Payment not found", paymentId, null, Status.ERRORED);
        }

        var card = cardOpt.get();
        var payment = paymentOpt.get();

        UUID stan = payment.getStan();

        Double cost = payment.getAmount();
        Double balance = card.getAmount();

        if (payment.getStatus() != Status.PSP_INITIATED) {
            return PayResponse.fail("Payment already tried.", paymentId, stan, Status.ERRORED);
        }

        var pspTs = payment.getPspTimestamp();
        Instant pspInstant = pspTs.atZone(ZoneId.systemDefault()).toInstant();
        if (Duration.between(pspInstant, Instant.now()).toMinutes() > 60) {
            return PayResponse.fail("Payment link expired.", paymentId, payment.getStan(), Status.FAILED);
        }

        if (cost == null || cost <= 0) {
            return PayResponse.fail("Invalid payment amount", paymentId, stan, Status.ERRORED);
        }
        if (balance == null) {
            return PayResponse.fail("Card balance is not set", paymentId, stan, Status.ERRORED);
        }

        if (balance < cost) {
            return PayResponse.fail("Insufficient funds", paymentId, stan, Status.FAILED);
        }

        Double newBalance = balance - cost;
        card.setAmount(newBalance);
        cardRepository.save(card);

        return PayResponse.ok(paymentId, cost, newBalance, stan);
    }

    @Transactional
    public PspUpdateStatusResponse getRedirectUrl(PayResponse response) {
        var paymentOpt = paymentRepository.findById(response.paymentId()).get();
        paymentOpt.setStatus(response.status());
        paymentRepository.save(paymentOpt);

        var pspStatus = new PspUpdateStatusRequest(
                response.status(),
                response.paymentId(),
                response.globalTransactionId(),
                response.acquirerTimestamp()
        );
        var pspStatusResp = pspClient.getBankUrl(pspStatus);
        return new PspUpdateStatusResponse(pspStatusResp.redirectUrl());
    }

    @Transactional
    public Double getAmount(UUID paymentId) {
        return paymentRepository.findById(paymentId).get().getAmount();
    }

    @Transactional
    public byte[] getQR(UUID paymentId) {
        var payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        String currency = payment.getCurrency();
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Payment currency is missing");
        }
        currency = currency.trim().toUpperCase(Locale.ROOT);

        Double amount = payment.getAmount();
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Payment amount is invalid");
        }

        String amountStr = amount.toString().replace('.', ',');

        String payload =
                "K:PR|V:01|C:1|R:105000000000000029|N:Web Shop|I:"
                        + currency + amountStr
                        + "|SF:221|S:Reservation";

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BitMatrix matrix = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 300, 300);
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR", e);
        }
    }
}
