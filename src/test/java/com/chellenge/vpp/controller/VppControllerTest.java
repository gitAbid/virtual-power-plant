package com.chellenge.vpp.controller;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.service.VppService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(VppController.class)
class VppControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private VppService vppService;

    @Test
    void getBatteriesByPostcodeRange_ValidRequest_ReturnsOk() {
        BatteryResponse response = new BatteryResponse(List.of("Battery1"), 100.0, 100.0);
        when(vppService.getBatteriesByPostcodeRange(any(), any(), any(), any()))
            .thenReturn(Mono.just(response));

        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "3000")
                .queryParam("minWattCapacity", "100")
                .queryParam("maxWattCapacity", "200")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.names[0]").isEqualTo("Battery1")
            .jsonPath("$.totalWattCapacity").isEqualTo(100.0)
            .jsonPath("$.averageWattCapacity").isEqualTo(100.0);
    }

    @Test
    void getBatteriesByPostcodeRange_MissingPostcode_ReturnsBadRequest() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .build())
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo("error")
            .jsonPath("$.message").isEqualTo("Validation failed")
            .jsonPath("$.errors.endPostcode").exists();
    }

    @Test
    void getBatteriesByPostcodeRange_InvalidPostcode_ReturnsBadRequest() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "abc")
                .queryParam("endPostcode", "3000")
                .build())
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo("error")
            .jsonPath("$.message").isEqualTo("Validation failed")
            .jsonPath("$.errors.startPostcode").exists();
    }

    @Test
    void getBatteriesByPostcodeRange_NegativeWattCapacity_ReturnsBadRequest() {
        webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "3000")
                .queryParam("minWattCapacity", "-100")
                .build())
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo("error")
            .jsonPath("$.message").isEqualTo("Validation failed")
            .jsonPath("$.errors.minWattCapacity").exists();
    }

    @Test
    void saveBatteries_ValidRequest_ReturnsOk() {
        List<BatteryDto> batteries = List.of(
            new BatteryDto("Battery1", "2000", 100.0)
        );

        when(vppService.saveBatteries(any())).thenReturn(Mono.empty());

        webClient.post()
            .uri("/api/v1/vpp/batteries")
            .bodyValue(batteries)
            .exchange()
            .expectStatus().isOk();
    }
}
