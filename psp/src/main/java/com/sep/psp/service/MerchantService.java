package com.sep.psp.service;

import com.sep.psp.dto.RegisterMerchantRequest;
import com.sep.psp.dto.RegisterMerchantResponse;
import com.sep.psp.model.Merchant;
import com.sep.psp.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegisterMerchantResponse createMerchant(RegisterMerchantRequest merchant) {
        Merchant newMerchant = Merchant.builder()
                .name(merchant.name())
                .password(passwordEncoder.encode(merchant.password()))
                .successUrl(merchant.successUrl())
                .failedUrl(merchant.failUrl())
                .errorUrl(merchant.errorUrl())
                .build();
        var saved = merchantRepository.save(newMerchant);
        return new RegisterMerchantResponse(saved.getId());
    }
}
