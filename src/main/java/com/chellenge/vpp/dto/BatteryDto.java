package com.chellenge.vpp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.NumberFormat;

public record BatteryDto(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Postcode is required")
    @NumberFormat(style = NumberFormat.Style.NUMBER)
    String postcode,
    
    @NotNull(message = "Watt capacity is required")
    @Positive(message = "Watt capacity must be positive")
    Double capacity
) {}
