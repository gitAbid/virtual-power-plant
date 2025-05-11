package com.challenge.vpp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record BatteryFilterRequest(
    @NotBlank(message = "Start postcode is required")
    String startPostcode,

    @NotBlank(message = "End postcode is required")
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

        if (startPostcode != null && endPostcode != null) {
            try {
                int start = Integer.parseInt(startPostcode);
                int end = Integer.parseInt(endPostcode);
                if (end < start) {
                    throw new IllegalArgumentException(
                        "End postcode must be greater than or equal to start postcode");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Postcodes must be valid integers");
            }
        }
    }
}
