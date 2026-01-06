package com.sep.psp.service;

import com.sep.psp.bank.BankClient;
import com.sep.psp.bank.BankGetUrlRequest;
import com.sep.psp.dto.*;
import com.sep.psp.model.BankPaymentUpdate;
import com.sep.psp.model.Merchant;
import com.sep.psp.model.Payment;
import com.sep.psp.model.Payment.Status;
import com.sep.psp.repository.BankMerchantInformationRepository;
import com.sep.psp.repository.BankPaymentUpdateRepository;
import com.sep.psp.repository.MerchantRepository;
import com.sep.psp.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PspPaymentService {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BankMerchantInformationRepository bankMerchantInformationRepository;
    @Autowired
    private BankPaymentUpdateRepository bankPaymentUpdateRepository;
    @Autowired
    private BankClient bankClient;

    private final String pspFrontendBaseUrl;

    public PspPaymentService(@Value("${psp.frontend.base-url:http://localhost:4201}") String pspFrontendBaseUrl) {
        this.pspFrontendBaseUrl = pspFrontendBaseUrl;
    }

    @Transactional
    public PspInitPaymentResponse initPayment(PspInitPaymentRequest req) {
        Merchant merchant = merchantRepository.findById(req.merchantId())
                .orElseThrow(() -> new UnauthorizedException("Invalid merchant credentials"));

        if (!merchant.getPassword().equals(req.merchantPassword())) {
            throw new UnauthorizedException("Invalid merchant credentials");
        }

        Payment payment = Payment.builder()
                .merchantId(req.merchantId())
                .merchantOrderId(req.merchantOrderId())
                .merchantTimestamp(req.merchantTimestamp())
                .amount(req.amount())
                .currency(req.currency().trim().toUpperCase())
                .status(Payment.Status.PSP_INITIATED)
                .build();

        paymentRepository.save(payment);

        String redirectUrl = pspFrontendBaseUrl + "/pay/" + payment.getPspPaymentId();

        return new PspInitPaymentResponse(redirectUrl, payment.getPspPaymentId());
    }

    @Transactional
    public CardPaymentResponse requestBankUrl(CardPaymentRequest req) {
        Payment payment = paymentRepository.findById(req.orderId()).get();
        UUID bankMerchantId = bankMerchantInformationRepository.findByMerchantId(payment.getMerchantId()).get().getBankMerchantId();
        var bankReq = new BankGetUrlRequest(
                bankMerchantId,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getPspPaymentId(),
                LocalDateTime.now()
        );

        var resp = bankClient.getBankUrl(bankReq);

        return new CardPaymentResponse(resp.paymentUrl());
    }

    @Transactional
    public BankRedirectResponse getStatusRedirect(BankRedirectRequest req) {
        Payment payment = paymentRepository.findById(req.globalTransactionId()).get();
        payment.setStatus(req.status());
        paymentRepository.save(payment);

        UUID merchantId = payment.getMerchantId();
        Merchant merchant = merchantRepository.findById(merchantId).get();
        String retVal;
        if (req.status().equals(Status.SUCCEEDED)) {
            retVal = merchant.getSuccessUrl();
        } else if (req.status().equals(Status.FAILED)) {
            retVal = merchant.getFailedUrl();
        } else {
            retVal = merchant.getErrorUrl();
        }

        var toSave = BankPaymentUpdate.builder()
                .status(req.status())
                .paymentId(req.paymentId())
                .globalTransactionId(req.globalTransactionId())
                .acquirerTimestamp(req.acquirerTimestamp())
                .build();

        bankPaymentUpdateRepository.save(toSave);

        return new BankRedirectResponse(retVal);
    }
}
