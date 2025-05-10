package com.chellenge.vpp.controller;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.service.VppService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * REST controller for Virtual Power Plant operations.
 * Provides endpoints for battery management with reactive support.
 */
@RestController
@RequestMapping("/api/v1/vpp")
public class VppController {
    
    private static final Logger logger = LoggerFactory.getLogger(VppController.class);
    private final VppService vppService;
    
    public VppController(VppService vppService) {
        this.vppService = vppService;
    }
    
    /**
     * Registers a list of batteries in the system.
     *
     * @param batteries List of batteries to register
     * @return Empty response when operation completes
     */
    @PostMapping("/batteries")
    public Mono<ResponseEntity<Void>> registerBatteries(@RequestBody @Valid List<BatteryDto> batteries) {
        logger.debug("Registering {} batteries", batteries.size());
        return vppService.saveBatteries(batteries)
            .map(ResponseEntity::ok)
            .doOnError(e -> logger.error("Error registering batteries", e));
    }
    
    /**
     * Retrieves batteries within the specified postcode range.
     *
     * @param startPostcode Starting postcode of the range
     * @param endPostcode Ending postcode of the range
     * @return Battery response containing matching batteries and statistics
     */
    @GetMapping("/batteries")
    public Mono<ResponseEntity<BatteryResponse>> getBatteriesByPostcodeRange(
            @RequestParam String startPostcode,
            @RequestParam String endPostcode) {
        logger.debug("Querying batteries between postcodes {} and {}", startPostcode, endPostcode);
        return vppService.getBatteriesByPostcodeRange(startPostcode, endPostcode)
            .map(ResponseEntity::ok)
            .doOnError(e -> logger.error("Error querying batteries by postcode range", e));
    }
}
