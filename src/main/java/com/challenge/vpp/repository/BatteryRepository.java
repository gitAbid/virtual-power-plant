package com.challenge.vpp.repository;

import com.challenge.vpp.entity.Battery;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BatteryRepository extends ReactiveCrudRepository<Battery, Long> {

    @Query("SELECT * FROM batteries WHERE postcode >= :startPostcode AND postcode <= :endPostcode " +
           "AND (:minWattCapacity IS NULL OR watt_capacity >= :minWattCapacity) " +
           "AND (:maxWattCapacity IS NULL OR watt_capacity <= :maxWattCapacity) " +
           "ORDER BY name")
    Flux<Battery> findBatteriesByPostcodeRangeAndWattCapacity(
            Integer startPostcode,
            Integer endPostcode,
            Double minWattCapacity,
            Double maxWattCapacity);
}
