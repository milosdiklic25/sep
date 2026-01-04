package com.sep.psp.dto;

import java.util.UUID;

public record CardPaymentRequest(
        UUID orderId
) {
}
