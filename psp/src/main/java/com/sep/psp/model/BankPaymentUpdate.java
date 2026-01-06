package com.sep.psp.model;

import jakarta.persistence.*;
import lombok.*;
import com.sep.psp.model.Payment.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "bank_payment_updates")
public class BankPaymentUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false, length = 20)
    private Status status;

    @Column(name="payment_id", nullable = false, updatable = false)
    private UUID paymentId;

    @Column(name="global_transaction_id")
    private UUID globalTransactionId;

    @Column(name="acquirer_timestamp")
    private LocalDateTime acquirerTimestamp;
}
