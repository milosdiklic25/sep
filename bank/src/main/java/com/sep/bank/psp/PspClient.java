package com.sep.bank.psp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PspClient {

    @Autowired
    private RestClient restClient;

    public PspUpdateStatusResponse getBankUrl(PspUpdateStatusRequest req) {
        return restClient.post()
                .uri("/api/payments/redirect")
                .contentType(MediaType.APPLICATION_JSON)
                .body(req)
                .retrieve()
                .body(PspUpdateStatusResponse.class);
    }
}
