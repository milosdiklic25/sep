package com.sep.web_shop.dto;

import java.time.LocalDate;
import java.util.UUID;

public record OrderRequest(
    UUID vehicleId,
    String username,
    LocalDate startDate,
    LocalDate endDate
){}
