package com.sep.bank.service;

import com.sep.bank.dto.GetRedirectUrlRequest;
import com.sep.bank.dto.GetRedirectUrlResponse;
import com.sep.bank.dto.PayRequest;
import com.sep.bank.dto.PayResponse;
import com.sep.bank.exception.MerchantNotFoundException;
import com.sep.bank.model.Payment;
import com.sep.bank.repository.BankMerchantRepository;
import com.sep.bank.repository.CardRepository;
import com.sep.bank.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {
    @Autowired
    private BankMerchantRepository bankMerchantRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CardRepository cardRepository;

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

        UUID paymentStan = UUID.randomUUID();
        Payment payment = Payment.builder()
                .bankMerchantId(req.bankMerchantId())
                .amount(req.amount())
                .currency(req.currency())
                .stan(paymentStan)
                .pspTimestamp(req.pspTimestamp())
                .build();
        Payment saved = paymentRepository.save(payment);
        String url = bankFrontendBaseUrl + "/pay/" + saved.getId().toString();

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
            return PayResponse.fail("Card not found or details are invalid", paymentId, null, Payment.Status.FAILED);
        }

        var paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            return PayResponse.fail("Payment not found", paymentId, null, Payment.Status.ERRORED);
        }

        var card = cardOpt.get();
        var payment = paymentOpt.get();

        UUID stan = payment.getStan();

        Double cost = payment.getAmount();
        Double balance = card.getAmount();

        if (cost == null || cost <= 0) {
            return PayResponse.fail("Invalid payment amount", paymentId, stan, Payment.Status.ERRORED);
        }
        if (balance == null) {
            return PayResponse.fail("Card balance is not set", paymentId, stan, Payment.Status.ERRORED);
        }

        if (balance < cost) {
            return PayResponse.fail("Insufficient funds", paymentId, stan, Payment.Status.FAILED);
        }

        Double newBalance = balance - cost;
        card.setAmount(newBalance);
        cardRepository.save(card);

        return PayResponse.ok(paymentId, cost, newBalance, stan);
    }
}
