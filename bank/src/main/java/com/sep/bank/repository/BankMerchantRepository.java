package com.sep.bank.repository;

import com.sep.bank.model.BankMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankMerchantRepository extends JpaRepository<BankMerchant, UUID> {
    Optional<BankMerchant> findByMerchantId(UUID merchantId);
}
