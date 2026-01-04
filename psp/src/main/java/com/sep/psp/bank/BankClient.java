package com.sep.psp.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BankClient {

    @Autowired
    private RestClient restClient;

    public BankGetUrlResponse getBankUrl(BankGetUrlRequest req) {
        return restClient.post()
                .uri("/api/payments/url")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(BankGetUrlResponse.class);
    }
}
