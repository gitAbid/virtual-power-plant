package com.chellenge.vpp.service;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service interface for Virtual Power Plant operations.
 * Handles battery registration and querying with reactive support.
 */
public interface VppService {
    /**
     * Saves a list of batteries to the system.
     *
     * @param batteries List of batteries to save
     * @return Empty Mono when operation completes
     */
    Mono<Void> saveBatteries(List<BatteryDto> batteries);

    /**
     * Retrieves batteries within a postcode range with statistics.
     *
     * @param startPostcode Starting postcode of the range
     * @param endPostcode Ending postcode of the range
     * @return Mono of BatteryResponse containing matching batteries and their statistics
     */
    Mono<BatteryResponse> getBatteriesByPostcodeRange(String startPostcode, String endPostcode);
}
