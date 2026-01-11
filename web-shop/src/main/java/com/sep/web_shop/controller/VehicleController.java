package com.sep.web_shop.controller;

import com.sep.web_shop.dto.OrderRequest;
import com.sep.web_shop.model.User;
import com.sep.web_shop.model.Vehicle;
import com.sep.web_shop.repository.OrderRepository;
import com.sep.web_shop.repository.UserRepository;
import com.sep.web_shop.repository.VehicleRepository;
import com.sep.web_shop.service.PaymentService;
import com.sep.web_shop.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.sep.web_shop.model.Order;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private UserRepository userRepository;

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
    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody OrderRequest req, Authentication auth) {
        if (req.vehicleId() == null || req.username() == null || req.startDate() == null || req.endDate() == null) {
            return ResponseEntity.badRequest().body("vehicleId, userId, startDate and endDate are required");
        }
        if (req.endDate().isBefore(req.startDate())) {
            return ResponseEntity.badRequest().body("endDate must be >= startDate");
        }

        Date start = Date.from(req.startDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(req.endDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        User user = userRepository.findByUsername(req.username());

        Order order = Order.builder()
                .userId(user.getId())
                .vehicleId(req.vehicleId())
                .startDate(start)
                .endDate(end)
                .merchantTimestamp(LocalDateTime.now())
                .amount(vehicleService.CalculateOrderAmount(req))
                .currency("RSD")
                .status(Order.Status.CREATED)
                .build();

        return ResponseEntity.ok(paymentService.createPayment1(order));
    }

}
