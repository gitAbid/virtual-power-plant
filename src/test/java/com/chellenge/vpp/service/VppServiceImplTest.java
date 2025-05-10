package com.chellenge.vpp.service;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.entity.Battery;
import com.chellenge.vpp.repository.BatteryRepository;
import com.chellenge.vpp.validator.BatteryValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class VppServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository;
    
    @Mock
    private BatteryValidator batteryValidator;

    private VppService vppService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vppService = new VppServiceImpl(batteryRepository, batteryValidator);
    }

    @Test
    void getBatteriesByPostcodeRange_WithoutWattCapacity_ReturnsAllInRange() {
        Battery battery1 = new Battery(1L, "Battery1", "2000", 100.0);
        Battery battery2 = new Battery(2L, "Battery2", "3000", 200.0);
        
        when(batteryValidator.validateWattCapacityParameters(any(), any()))
            .thenReturn(Mono.empty());
        when(batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
            anyString(), anyString(), any(), any()))
            .thenReturn(Flux.just(battery1, battery2));

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", null, null))
            .expectNext(new BatteryResponse(List.of("Battery1", "Battery2"), 300.0, 150.0))
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_WithWattCapacity_ReturnsFilteredResults() {
        Battery battery2 = new Battery(2L, "Battery2", "3000", 200.0);
        
        when(batteryValidator.validateWattCapacityParameters(any(), any()))
            .thenReturn(Mono.empty());
        when(batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
            anyString(), anyString(), any(), any()))
            .thenReturn(Flux.just(battery2));

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", 150.0, 250.0))
            .expectNext(new BatteryResponse(List.of("Battery2"), 200.0, 200.0))
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_EmptyResult_ReturnsEmptyResponse() {
        when(batteryValidator.validateWattCapacityParameters(any(), any()))
            .thenReturn(Mono.empty());
        when(batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
            anyString(), anyString(), any(), any()))
            .thenReturn(Flux.empty());

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", null, null))
            .expectNext(new BatteryResponse(List.of(), 0.0, 0.0))
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_NegativeMinWattCapacity_ThrowsException() {
        when(batteryValidator.validateWattCapacityParameters(any(), any()))
            .thenReturn(Mono.error(new IllegalArgumentException("Minimum watt capacity cannot be negative")));

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", -100.0, null))
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    @Test
    void getBatteriesByPostcodeRange_NegativeMaxWattCapacity_ThrowsException() {
        when(batteryValidator.validateWattCapacityParameters(any(), any()))
            .thenReturn(Mono.error(new IllegalArgumentException("Maximum watt capacity cannot be negative")));

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", null, -200.0))
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    @Test
    void getBatteriesByPostcodeRange_MaxLessThanMin_ThrowsException() {
        when(batteryValidator.validateWattCapacityParameters(any(), any()))
            .thenReturn(Mono.error(new IllegalArgumentException(
                "Maximum watt capacity must be greater than or equal to minimum watt capacity")));

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", 200.0, 100.0))
            .expectError(IllegalArgumentException.class)
            .verify();
    }

    @Test
    void saveBatteries_Success() {
        List<BatteryDto> batteryDtos = List.of(
            new BatteryDto("Battery1", "2000", 100.0),
            new BatteryDto("Battery2", "3000", 200.0)
        );

        when(batteryRepository.saveAll(any(Iterable.class))).thenReturn(Flux.empty());

        StepVerifier.create(vppService.saveBatteries(batteryDtos))
            .verifyComplete();
    }
}
