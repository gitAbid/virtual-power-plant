package com.chellenge.vpp.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class BatteryValidatorTest {

    private BatteryValidator batteryValidator;

    @BeforeEach
    void setUp() {
        batteryValidator = new BatteryValidator();
    }

    @Test
    void validateWattCapacityParameters_ValidParameters_ReturnsEmpty() {
        StepVerifier.create(batteryValidator.validateWattCapacityParameters(100.0, 200.0))
            .verifyComplete();
    }

    @Test
    void validateWattCapacityParameters_NullParameters_ReturnsEmpty() {
        StepVerifier.create(batteryValidator.validateWattCapacityParameters(null, null))
            .verifyComplete();
    }

    @Test
    void validateWattCapacityParameters_NegativeMin_ThrowsException() {
        StepVerifier.create(batteryValidator.validateWattCapacityParameters(-100.0, 200.0))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException && 
                throwable.getMessage().equals("Minimum watt capacity cannot be negative"))
            .verify();
    }

    @Test
    void validateWattCapacityParameters_NegativeMax_ThrowsException() {
        StepVerifier.create(batteryValidator.validateWattCapacityParameters(100.0, -200.0))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException && 
                throwable.getMessage().equals("Maximum watt capacity cannot be negative"))
            .verify();
    }

    @Test
    void validateWattCapacityParameters_MaxLessThanMin_ThrowsException() {
        StepVerifier.create(batteryValidator.validateWattCapacityParameters(200.0, 100.0))
            .expectErrorMatches(throwable -> 
                throwable instanceof IllegalArgumentException && 
                throwable.getMessage().equals("Maximum watt capacity must be greater than or equal to minimum watt capacity"))
            .verify();
    }
}
