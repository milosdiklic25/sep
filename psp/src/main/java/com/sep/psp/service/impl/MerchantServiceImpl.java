package com.sep.psp.service.impl;

import com.sep.psp.model.Merchant;
import com.sep.psp.repository.MerchantRepository;
import com.sep.psp.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImpl implements MerchantService {
    @Autowired
    private MerchantRepository merchantRepository;

    public Merchant createMerchant(Merchant merchant) {
        //TODO: send http request to merchant for future reference
        return merchantRepository.save(merchant);
    }
}
