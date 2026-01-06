package com.sep.bank.repository;

import com.sep.bank.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Optional<Card> findByCardholderNameAndCardNumberAndExpiryDateAndCvv(
            String cardholderName,
            String cardNumber,
            String expiryDate,
            String cvv
    );
}
