package com.sep.web_shop.repository;

import com.sep.web_shop.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
}
