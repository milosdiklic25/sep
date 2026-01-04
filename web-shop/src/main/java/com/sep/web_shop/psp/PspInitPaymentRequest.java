package com.sep.web_shop.psp;

import java.time.LocalDateTime;
import java.util.UUID;

public record PspInitPaymentRequest(
        UUID merchantId,
        String merchantPassword,
        Double amount,
        String currency,
        UUID merchantOrderId,
        LocalDateTime merchantTimestamp
) {
}
