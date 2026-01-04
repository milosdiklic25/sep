package com.sep.psp.controller;

import com.sep.psp.model.Merchant;
import com.sep.psp.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {
    @Autowired
    MerchantService merchantService;
    @PostMapping
    public ResponseEntity<Merchant> createMerchant(@RequestBody Merchant request) {
        Merchant created = merchantService.createMerchant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
