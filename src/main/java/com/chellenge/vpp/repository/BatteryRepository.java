package com.chellenge.vpp.repository;

import com.chellenge.vpp.entity.Battery;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BatteryRepository extends ReactiveCrudRepository<Battery, Long> {
    
    @Query("SELECT * FROM batteries WHERE postcode >= :startPostcode AND postcode <= :endPostcode ORDER BY name")
    Flux<Battery> findBatteriesByPostcodeRange(String startPostcode, String endPostcode);
}
