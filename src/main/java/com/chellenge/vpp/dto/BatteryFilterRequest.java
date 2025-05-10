package com.chellenge.vpp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record BatteryFilterRequest(
    @NotBlank(message = "Start postcode is required")
    @Pattern(regexp = "\\d{4}", message = "Start postcode must be a 4-digit number")
    String startPostcode,

    @NotBlank(message = "End postcode is required")
    @Pattern(regexp = "\\d{4}", message = "End postcode must be a 4-digit number")
    String endPostcode,

    @PositiveOrZero(message = "Minimum watt capacity cannot be negative")
    Double minWattCapacity,

    @PositiveOrZero(message = "Maximum watt capacity cannot be negative")
    Double maxWattCapacity
) {
    public BatteryFilterRequest {
        if (minWattCapacity != null && maxWattCapacity != null && maxWattCapacity < minWattCapacity) {
            throw new IllegalArgumentException(
                "Maximum watt capacity must be greater than or equal to minimum watt capacity");
        }
    }
}
