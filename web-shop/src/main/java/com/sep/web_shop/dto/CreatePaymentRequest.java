package com.sep.web_shop.dto;

public record CreatePaymentRequest(
        Double amount,
        String currency
) {}
