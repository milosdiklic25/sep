package com.sep.web_shop.psp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class PspClient {

    @Autowired
    private RestClient restClient;

    public PspInitPaymentResponse initPayment(PspInitPaymentRequest req) {
        return restClient.post()
                .uri("/api/payments/init")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(PspInitPaymentResponse.class);
    }
}
