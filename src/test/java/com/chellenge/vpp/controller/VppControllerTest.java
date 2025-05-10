package com.chellenge.vpp.controller;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryFilterRequest;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.service.VppService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(VppController.class)
class VppControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VppService vppService;

    @Test
    void registerBatteries_Success() {
        List<BatteryDto> batteries = List.of(
            new BatteryDto("Battery1", "2000", 12.5),
            new BatteryDto("Battery2", "2001", 15.0)
        );

        when(vppService.saveBatteries(any())).thenReturn(Mono.empty());

        webTestClient.post()
            .uri("/api/v1/vpp/batteries")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(batteries)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void getBatteriesByPostcodeRange_Success() {
        BatteryResponse response = new BatteryResponse(
            List.of("Battery1", "Battery2"),
            27.5,
            13.75
        );

        when(vppService.getBatteriesByPostcodeRange(anyString(), anyString(), any(), any()))
            .thenReturn(Mono.just(response));

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "2001")
                .queryParam("minWattCapacity", "10.0")
                .queryParam("maxWattCapacity", "20.0")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody(BatteryResponse.class)
            .isEqualTo(response);
    }

    @Test
    void getBatteriesByPostcodeRange_InvalidPostcode_ReturnsBadRequest() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "")
                .queryParam("endPostcode", "2001")
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void getBatteriesByPostcodeRange_InvalidWattCapacity_ReturnsBadRequest() {
        webTestClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "2001")
                .queryParam("minWattCapacity", "-10.0")
                .build())
            .exchange()
            .expectStatus().isBadRequest();
    }
}
