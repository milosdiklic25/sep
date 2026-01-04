package com.sep.psp.bank;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class BankRestClientConfig {

    @Bean
    RestClient bankRestClient() {
        return RestClient.builder()
                .baseUrl("http://localhost:8082")
                .build();
    }
}
