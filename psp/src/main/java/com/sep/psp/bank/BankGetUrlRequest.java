package com.sep.psp.bank;

import java.time.LocalDateTime;
import java.util.UUID;

public record BankGetUrlRequest(
        UUID bankMerchantId,
        Double amount,
        String currency,
        UUID pspPaymentId,
        LocalDateTime pspTimestamp
) {
}
