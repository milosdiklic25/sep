package com.sep.web_shop.service;

import com.sep.web_shop.model.MerchantInformation;
import com.sep.web_shop.model.Order;
import com.sep.web_shop.dto.CreatePaymentRequest;
import com.sep.web_shop.dto.CreatePaymentResponse;
import com.sep.web_shop.psp.PspClient;
import com.sep.web_shop.psp.PspInitPaymentRequest;
import com.sep.web_shop.rabbitmq.RabbitMQConfig;
import com.sep.web_shop.repository.MerchantInformationRepository;
import com.sep.web_shop.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {
    @Autowired
    private MerchantInformationRepository merchantRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PspClient pspClient;

    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        MerchantInformation merchant = merchantRepository.findTopByOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("Merchant credentials not configured"));

        Order order = Order.builder()
                .merchantTimestamp(LocalDateTime.now())
                .amount(request.amount())
                .currency(request.currency().trim().toUpperCase())
                .status(Order.Status.PSP_INITIATED)
                .build();
        orderRepository.save(order);

        var pspReq = new PspInitPaymentRequest(
                merchant.getMerchantId(),
                merchant.getMerchantPassword(),
                order.getAmount(),
                order.getCurrency(),
                order.getMerchantOrderId(),
                order.getMerchantTimestamp()
        );

        var pspResp = pspClient.initPayment(pspReq);

        return new CreatePaymentResponse(order.getMerchantOrderId(), pspResp.redirectUrl());
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}
