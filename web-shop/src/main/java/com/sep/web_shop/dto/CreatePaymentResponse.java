package com.sep.web_shop.dto;

import java.util.UUID;

public record CreatePaymentResponse(
        UUID merchantOrderId,
        String redirectUrl
) {}
