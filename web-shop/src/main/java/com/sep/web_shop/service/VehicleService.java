package com.sep.web_shop.service;

import com.sep.web_shop.dto.OrderRequest;
import com.sep.web_shop.model.Vehicle;
import com.sep.web_shop.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public double CalculateOrderAmount(OrderRequest req){
        Vehicle vehicle = vehicleRepository.findById(req.vehicleId())
                .orElse(null);
        long days = ChronoUnit.DAYS.between(req.startDate(), req.endDate()) + 1;
        return days * vehicle.getPricePerDay();
    }
}
