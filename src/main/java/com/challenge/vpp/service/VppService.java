package com.challenge.vpp.service;

import com.challenge.vpp.dto.BatteryDto;
import com.challenge.vpp.dto.BatteryResponse;
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
     * Retrieves batteries within a postcode range with optional watt capacity filtering.
     *
     * @param startPostcode Starting postcode of the range
     * @param endPostcode Ending postcode of the range
     * @param minWattCapacity Minimum watt capacity (optional)
     * @param maxWattCapacity Maximum watt capacity (optional)
     * @return Mono of BatteryResponse containing matching batteries and their statistics
     */
    Mono<BatteryResponse> getBatteriesByPostcodeRange(
            String startPostcode,
            String endPostcode,
            Double minWattCapacity,
            Double maxWattCapacity);
}
