package com.sep.psp.dto;

import com.sep.psp.model.Payment.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record BankRedirectRequest(
        Status status,
        UUID paymentId,
        UUID globalTransactionId,
        LocalDateTime acquirerTimestamp
) {
}
