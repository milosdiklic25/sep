package com.sep.bank.service;

import com.sep.bank.dto.GetRedirectUrlRequest;
import com.sep.bank.dto.GetRedirectUrlResponse;
import com.sep.bank.repository.BankMerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    @Autowired
    private BankMerchantRepository bankMerchantRepository;

    private final String bankFrontendBaseUrl;

    public PaymentService(@Value("${bank.frontend.base-url:http://localhost:4202}") String bankFrontendBaseUrl) {
        this.bankFrontendBaseUrl = bankFrontendBaseUrl;
    }

    @Transactional
    public GetRedirectUrlResponse generateRedirectUrl(GetRedirectUrlRequest request) {
        var merchant = bankMerchantRepository.findByMerchantId(request.bankMerchantId());
        if (merchant.isEmpty()) {
            return null;
        }

        return null;
    }
}
