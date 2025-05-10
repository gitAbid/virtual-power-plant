package com.chellenge.vpp.controller;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.service.VppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VppControllerTest {

    @Mock
    private VppService vppService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        VppController vppController = new VppController(vppService);
        webTestClient = WebTestClient.bindToController(vppController).build();
    }

    @Test
    void registerBatteries_ValidInput_ReturnsOk() {
        // Given
        List<BatteryDto> batteries = List.of(
            new BatteryDto("Battery1", "2000", 100.0),
            new BatteryDto("Battery2", "2001", 200.0)
        );
        when(vppService.saveBatteries(anyList())).thenReturn(Mono.empty());

        // When & Then
        webTestClient.post()
            .uri("/api/v1/vpp/batteries")
            .bodyValue(batteries)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void getBatteriesByPostcodeRange_ValidRange_ReturnsBatteries() {
        // Given
        BatteryResponse expectedResponse = new BatteryResponse(
            List.of("Battery1", "Battery2"),
            300.0,
            150.0
        );
        when(vppService.getBatteriesByPostcodeRange(anyString(), anyString()))
            .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "2001")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BatteryResponse.class)
            .isEqualTo(expectedResponse);
    }

    @Test
    void registerBatteries_InvalidInput_ReturnsBadRequest() {
        // Given
        List<BatteryDto> batteries = List.of(
            new BatteryDto("", "2000", -100.0)  // Invalid data
        );

        // When & Then
        webTestClient.post()
            .uri("/api/v1/vpp/batteries")
            .bodyValue(batteries)
            .exchange()
            .expectStatus().isBadRequest();
    }
}
