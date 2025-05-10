package com.chellenge.vpp.service;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.entity.Battery;
import com.chellenge.vpp.repository.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VppServiceImplTest {

    @Mock
    private BatteryRepository batteryRepository;

    private VppService vppService;

    @BeforeEach
    void setUp() {
        vppService = new VppServiceImpl(batteryRepository);
    }

    @Test
    void saveBatteries_ValidInput_SavesSuccessfully() {
        // Given
        List<BatteryDto> batteries = List.of(
            new BatteryDto("Battery1", "2000", 100.0),
            new BatteryDto("Battery2", "2001", 200.0)
        );
        when(batteryRepository.saveAll(anyList())).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(vppService.saveBatteries(batteries))
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_ValidRange_ReturnsCorrectResponse() {
        // Given
        String startPostcode = "2000";
        String endPostcode = "2001";
        List<Battery> batteries = List.of(
            new Battery(1L, "Battery1", "2000", 100.0),
            new Battery(2L, "Battery2", "2001", 200.0)
        );
        when(batteryRepository.findBatteriesByPostcodeRange(startPostcode, endPostcode))
            .thenReturn(Flux.fromIterable(batteries));

        // When & Then
        StepVerifier.create(vppService.getBatteriesByPostcodeRange(startPostcode, endPostcode))
            .expectNext(new BatteryResponse(
                List.of("Battery1", "Battery2"),
                300.0,
                150.0
            ))
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_EmptyResult_ReturnsEmptyResponse() {
        // Given
        when(batteryRepository.findBatteriesByPostcodeRange(any(), any()))
            .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "2001"))
            .expectNext(new BatteryResponse(List.of(), 0.0, 0.0))
            .verifyComplete();
    }
}
