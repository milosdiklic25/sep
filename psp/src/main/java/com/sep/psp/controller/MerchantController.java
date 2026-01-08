package com.sep.psp.controller;

import com.sep.psp.dto.RegisterMerchantRequest;
import com.sep.psp.dto.RegisterMerchantResponse;
import com.sep.psp.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/register")
    public ResponseEntity<RegisterMerchantResponse> createMerchant(@RequestBody RegisterMerchantRequest request) {
        return ResponseEntity.ok(merchantService.createMerchant(request));
    }
}
