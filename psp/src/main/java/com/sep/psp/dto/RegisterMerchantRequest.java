package com.sep.psp.dto;

public record RegisterMerchantRequest(
        String name,
        String password,
        String errorUrl,
        String successUrl,
        String failUrl
) {
}
