package com.sep.web_shop.dto;

import com.sep.web_shop.model.Order.Status;

import java.util.UUID;

public record PaymentStatusMessage(
        UUID orderId,
        Status status
) {}
