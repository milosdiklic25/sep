package com.sep.web_shop.psp;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class PspClient {

    // Mocked PSP call
    public PspInitPaymentResponse initPayment(PspInitPaymentRequest req) {
        // In a real implementation, you'd do HTTP POST to PSP backend.
        // Here we just return a fake PSP frontend URL that includes the merchantOrderId.
        String encodedOrderId = URLEncoder.encode(String.valueOf(req.merchantOrderId()), StandardCharsets.UTF_8);
        String redirect = "http://localhost:4201/psp/pay?merchantOrderId=" + encodedOrderId;
        return new PspInitPaymentResponse(redirect);
    }
}
