package com.chellenge.vpp.service;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.entity.Battery;
import com.chellenge.vpp.repository.BatteryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
public class VppServiceImpl implements VppService {
    
    private static final Logger logger = LoggerFactory.getLogger(VppServiceImpl.class);
    private final BatteryRepository batteryRepository;
    
    public VppServiceImpl(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
        logger.info("VppService initialized with battery repository");
    }
    
    @Override
    public Mono<Void> saveBatteries(List<BatteryDto> batteries) {
        logger.info("Processing request to save {} batteries", batteries.size());
        return Mono.just(batteries)
            .map(batteryDtos -> {
                logger.debug("Converting battery DTOs to entities");
                return batteryDtos.stream()
                    .map(dto -> Battery.from(dto.name(), Integer.valueOf(dto.postcode()), dto.capacity()))
                    .toList();
            })
            .flatMap(batteryEntities -> {
                logger.debug("Saving {} battery entities to database", batteryEntities.size());
                return batteryRepository.saveAll(batteryEntities).then();
            })
            .doOnSuccess(v -> logger.info("Successfully saved {} batteries", batteries.size()))
            .doOnError(e -> logger.error("Failed to save batteries", e));
    }
    
    @Override
    public Mono<BatteryResponse> getBatteriesByPostcodeRange(
            String startPostcode,
            String endPostcode,
            Double minWattCapacity,
            Double maxWattCapacity) {
        logger.info("Querying batteries with postcodes [{} - {}] and watt capacity range [{} - {}]", 
                startPostcode, endPostcode, minWattCapacity, maxWattCapacity);
        return Mono.defer(() -> batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
                                Integer.valueOf(startPostcode),
                                Integer.valueOf(endPostcode),
                                minWattCapacity,
                                maxWattCapacity)
                        .collectList())
            .map(batteries -> {
                logger.debug("Found {} batteries matching criteria", batteries.size());
                List<String> names = batteries.stream()
                    .map(Battery::name)
                    .sorted()
                    .toList();
                    
                double totalCapacity = batteries.stream()
                    .mapToDouble(Battery::wattCapacity)
                    .sum();
                    
                double avgCapacity = batteries.isEmpty() ? 0 : 
                    totalCapacity / batteries.size();
                
                logger.debug("Calculated statistics: total capacity={}, average capacity={}", 
                        totalCapacity, avgCapacity);
                return new BatteryResponse(names, totalCapacity, avgCapacity);
            })
            .doOnError(e -> logger.error("Error querying batteries by postcode range", e));
    }
}
