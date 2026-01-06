package com.sep.bank.dto;

import java.util.UUID;

public record GetRedirectUrlResponse(
        String paymentUrl,
        UUID paymentId
) {
}
