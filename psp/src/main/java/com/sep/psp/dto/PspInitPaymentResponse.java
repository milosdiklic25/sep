package com.sep.psp.dto;

import java.util.UUID;

public record PspInitPaymentResponse(
        String redirectUrl,
        UUID pspPaymentId
) {}
