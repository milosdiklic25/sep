package com.sep.psp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "payments")
public class Payment {

    public enum Status { INITIATED }

    @Id
    @UuidGenerator
    @Column(name="psp_payment_id", nullable = false, updatable = false)
    private UUID pspPaymentId;

    @Column(name="merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name="merchant_order_id", nullable = false)
    private UUID merchantOrderId;

    @Column(name="merchant_timestamp", nullable = false)
    private LocalDateTime merchantTimestamp;

    @Column(name="amount", nullable = false)
    private Double amount;

    @Column(name="currency", nullable = false, length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 20)
    private Status status;
}
