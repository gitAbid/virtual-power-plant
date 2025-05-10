package com.chellenge.vpp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BatteryDto(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Postcode is required")
    String postcode,
    
    @NotNull(message = "Watt capacity is required")
    @Positive(message = "Watt capacity must be positive")
    Double capacity
) {}
