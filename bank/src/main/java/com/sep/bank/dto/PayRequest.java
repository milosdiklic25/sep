package com.sep.bank.dto;

public record PayRequest(
        String cardholderName,
        String cardNumber,
        String expiryDate,
        String cvv
) {
}
