package com.sep.web_shop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {
    public enum Status { CREATED, PSP_INITIATED, FAILED, ERRORED, SUCCEEDED }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID merchantOrderId;

    @Column(name="user_id", nullable = false)
    private UUID userId;

    @Column(name="vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name="start_date", nullable = false)
    private Date startDate;

    @Column(name="end_date", nullable = false)
    private Date endDate;

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
