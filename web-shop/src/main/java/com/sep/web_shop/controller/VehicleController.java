package com.sep.web_shop.controller;

import com.sep.web_shop.model.Vehicle;
import com.sep.web_shop.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping
    public List<Vehicle> getVehicles() {
        return vehicleRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable UUID id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
