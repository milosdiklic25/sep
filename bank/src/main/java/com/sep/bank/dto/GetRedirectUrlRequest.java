package com.sep.bank.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetRedirectUrlRequest(
        UUID bankMerchantId,
        Double amount,
        String currency,
        UUID pspPaymentId,
        LocalDateTime pspTimestamp
) {
}