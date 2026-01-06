package com.sep.psp.repository;

import com.sep.psp.model.BankPaymentUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankPaymentUpdateRepository extends JpaRepository<BankPaymentUpdate, UUID> {
}
