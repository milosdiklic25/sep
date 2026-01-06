package com.sep.bank.dto;

import com.sep.bank.model.Payment.Status;

import java.time.LocalDateTime;
import java.util.UUID;

public record PayResponse(
        boolean success,
        String message,
        UUID paymentId,
        Double amountCharged,
        Double newBalance,
        UUID globalTransactionId,
        LocalDateTime acquirerTimestamp,
        Status status
) {
    public static PayResponse ok(UUID paymentId, Double amountCharged, Double newBalance, UUID stan) {
        return new PayResponse(true, "Payment successful", paymentId, amountCharged, newBalance, stan, LocalDateTime.now(), Status.SUCCEEDED);
    }

    public static PayResponse fail(String message, UUID paymentId, UUID stan, Status status) {
        return new PayResponse(false, message, paymentId, null, null, stan, LocalDateTime.now(), status);
    }
}
