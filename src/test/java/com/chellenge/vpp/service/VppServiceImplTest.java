package com.chellenge.vpp.service;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.entity.Battery;
import com.chellenge.vpp.repository.BatteryRepository;
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

    private VppService vppService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vppService = new VppServiceImpl(batteryRepository);
    }

    @Test
    void saveBatteries_Success() {
        List<BatteryDto> batteryDtos = List.of(
            new BatteryDto("Battery1", "2000", 12.5),
            new BatteryDto("Battery2", "2001", 15.0)
        );

        when(batteryRepository.saveAll(any(Iterable.class))).thenReturn(Flux.empty());

        StepVerifier.create(vppService.saveBatteries(batteryDtos))
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_Success() {
        String startPostcode = "2000";
        String endPostcode = "2001";
        Double minWattCapacity = 10.0;
        Double maxWattCapacity = 20.0;

        List<Battery> batteries = List.of(
            Battery.from("Battery1", "2000", 12.5),
            Battery.from("Battery2", "2001", 15.0)
        );

        when(batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
            startPostcode, endPostcode, minWattCapacity, maxWattCapacity))
            .thenReturn(Flux.fromIterable(batteries));

        BatteryResponse expectedResponse = new BatteryResponse(
            List.of("Battery1", "Battery2"),
            27.5,
            13.75
        );

        StepVerifier.create(vppService.getBatteriesByPostcodeRange(
            startPostcode, endPostcode, minWattCapacity, maxWattCapacity))
            .expectNext(expectedResponse)
            .verifyComplete();
    }

    @Test
    void getBatteriesByPostcodeRange_EmptyResult() {
        when(batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
            anyString(), anyString(), any(), any()))
            .thenReturn(Flux.empty());

        BatteryResponse expectedResponse = new BatteryResponse(
            List.of(),
            0.0,
            0.0
        );

        StepVerifier.create(vppService.getBatteriesByPostcodeRange("2000", "3000", null, null))
            .expectNext(expectedResponse)
            .verifyComplete();
    }
}
