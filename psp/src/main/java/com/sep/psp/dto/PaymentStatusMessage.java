package com.sep.psp.dto;

import com.sep.psp.model.Payment.Status;

import java.util.UUID;

public record PaymentStatusMessage(
        UUID orderId,
        Status status
) {}
