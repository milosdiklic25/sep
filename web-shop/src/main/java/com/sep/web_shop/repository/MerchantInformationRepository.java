package com.sep.web_shop.repository;

import com.sep.web_shop.model.MerchantInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantInformationRepository extends JpaRepository<MerchantInformation, UUID> {
    Optional<MerchantInformation> findTopByOrderByIdAsc();
}
