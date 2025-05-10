package com.chellenge.vpp.validator;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BatteryValidator {

    public Mono<Void> validateWattCapacityParameters(Double minWattCapacity, Double maxWattCapacity) {
        if (minWattCapacity != null && minWattCapacity < 0) {
            return Mono.error(new IllegalArgumentException("Minimum watt capacity cannot be negative"));
        }

        if (maxWattCapacity != null && maxWattCapacity < 0) {
            return Mono.error(new IllegalArgumentException("Maximum watt capacity cannot be negative"));
        }

        if (minWattCapacity != null && maxWattCapacity != null && maxWattCapacity < minWattCapacity) {
            return Mono.error(new IllegalArgumentException(
                    "Maximum watt capacity must be greater than or equal to minimum watt capacity"));
        }

        return Mono.empty();
    }
}
