package com.sep.bank.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "payments")
public class Payment {
    public enum Status { CREATED, PSP_INITIATED, FAILED, ERRORED, SUCCEEDED }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID bankMerchantId;
    private Double amount;
    private String currency;
    private UUID stan;
    private LocalDateTime pspTimestamp;
    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 20)
    private Status status;
}
