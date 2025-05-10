package com.chellenge.vpp.controller;

import com.chellenge.vpp.config.AbstractIntegrationTest;
import com.chellenge.vpp.entity.Battery;
import com.chellenge.vpp.repository.BatteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class VppControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BatteryRepository batteryRepository;

    @BeforeEach
    void setUp() {
        // Clear existing data before each test
        batteryRepository.deleteAll()
            .as(StepVerifier::create)
            .verifyComplete();
    }

    @Test
    void contextLoads() {
        assertThat(webTestClient).isNotNull();
        assertThat(batteryRepository).isNotNull();
    }

    @Test
    void shouldReturnEmptyListWhenNoBatteriesExist() {
        webTestClient.get()
            .uri(builder -> builder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "0000")
                .queryParam("endPostcode", "9999")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.names").isArray()
            .jsonPath("$.names.length()").isEqualTo(0)
            .jsonPath("$.totalWattCapacity").isEqualTo(0)
            .jsonPath("$.averageWattCapacity").isEqualTo(0);
    }

    @Test
    void shouldReturnBatteriesWithinPostcodeRange() {
        // Given
        List<Battery> batteries = List.of(
            Battery.from("Battery1", 2000, 10000.0),
            Battery.from("Battery2", 3000, 15000.0),
            Battery.from("Battery3", 4000, 20000.0),
            Battery.from("OutOfRange", 5000, 25000.0)
        );

        batteryRepository.saveAll(Flux.fromIterable(batteries))
            .as(StepVerifier::create)
            .expectNextCount(4)
            .verifyComplete();

        // When & Then
        webTestClient.get()
            .uri(builder -> builder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "4000")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.names").isArray()
            .jsonPath("$.names.length()").isEqualTo(3)
            .jsonPath("$.names[0]").isEqualTo("Battery1")
            .jsonPath("$.names[1]").isEqualTo("Battery2")
            .jsonPath("$.names[2]").isEqualTo("Battery3")
            .jsonPath("$.totalWattCapacity").isEqualTo(45000.0)
            .jsonPath("$.averageWattCapacity").isEqualTo(15000.0);
    }

    @Test
    void shouldFilterBatteriesByWattCapacity() {
        // Given
        List<Battery> batteries = List.of(
            Battery.from("LowCapacity", 2000, 5000.0),
            Battery.from("MediumCapacity", 2000, 15000.0),
            Battery.from("HighCapacity", 2000, 25000.0)
        );

        batteryRepository.saveAll(Flux.fromIterable(batteries))
            .as(StepVerifier::create)
            .expectNextCount(3)
            .verifyComplete();

        // When & Then
        webTestClient.get()
            .uri(builder -> builder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "2000")
                .queryParam("endPostcode", "2000")
                .queryParam("minWattCapacity", "10000")
                .queryParam("maxWattCapacity", "20000")
                .build())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.names").isArray()
            .jsonPath("$.names.length()").isEqualTo(1)
            .jsonPath("$.names[0]").isEqualTo("MediumCapacity")
            .jsonPath("$.totalWattCapacity").isEqualTo(15000.0)
            .jsonPath("$.averageWattCapacity").isEqualTo(15000.0);
    }

    @Test
    void shouldReturnBadRequestForInvalidPostcodeRange() {
        webTestClient.get()
            .uri(builder -> builder
                .path("/api/v1/vpp/batteries")
                .queryParam("startPostcode", "4000")
                .queryParam("endPostcode", "2000")  // endPostcode < startPostcode
                .build())
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo("error")
            .jsonPath("$.message").isNotEmpty();
    }
}
