package com.sep.psp.repository;

import com.sep.psp.model.BankMerchantInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankMerchantInformationRepository extends JpaRepository<BankMerchantInformation, UUID> {
    Optional<BankMerchantInformation> findByMerchantId(UUID merchantId);
}
