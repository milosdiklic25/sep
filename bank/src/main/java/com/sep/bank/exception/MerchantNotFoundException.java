package com.sep.bank.exception;

import java.util.UUID;

public class MerchantNotFoundException extends RuntimeException {
    public MerchantNotFoundException(UUID merchantId) {
        super("Bank merchant not found for merchantId=" + merchantId.toString());
    }
}
