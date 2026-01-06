package com.sep.psp.bank;

import java.util.UUID;

public record BankGetUrlResponse(
        String paymentUrl,
        UUID paymentId
) {
}
