package com.sep.psp.service;

import com.sep.psp.dto.PspInitPaymentRequest;
import com.sep.psp.dto.PspInitPaymentResponse;
import com.sep.psp.model.Merchant;
import com.sep.psp.model.Payment;
import com.sep.psp.repository.MerchantRepository;
import com.sep.psp.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PspPaymentService {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private PaymentRepository paymentRepository;

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
                .status(Payment.Status.INITIATED)
                .build();

        paymentRepository.save(payment);

        String redirectUrl = pspFrontendBaseUrl + "/pay/" + payment.getPspPaymentId();

        return new PspInitPaymentResponse(redirectUrl, payment.getPspPaymentId());
    }
}
