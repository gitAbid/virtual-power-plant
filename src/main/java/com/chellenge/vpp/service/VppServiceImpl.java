package com.chellenge.vpp.service;

import com.chellenge.vpp.dto.BatteryDto;
import com.chellenge.vpp.dto.BatteryResponse;
import com.chellenge.vpp.entity.Battery;
import com.chellenge.vpp.repository.BatteryRepository;
import com.chellenge.vpp.validator.BatteryValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
public class VppServiceImpl implements VppService {
    
    private final BatteryRepository batteryRepository;
    private final BatteryValidator batteryValidator;
    
    public VppServiceImpl(BatteryRepository batteryRepository, BatteryValidator batteryValidator) {
        this.batteryRepository = batteryRepository;
        this.batteryValidator = batteryValidator;
    }
    
    @Override
    public Mono<Void> saveBatteries(List<BatteryDto> batteries) {
        return Mono.just(batteries)
            .map(batteryDtos -> batteryDtos.stream()
                .map(dto -> Battery.from(dto.name(), dto.postcode(), dto.wattCapacity()))
                .toList())
            .flatMap(batteryEntities -> batteryRepository.saveAll(batteryEntities).then());
    }
    
    @Override
    public Mono<BatteryResponse> getBatteriesByPostcodeRange(
            String startPostcode,
            String endPostcode,
            Double minWattCapacity,
            Double maxWattCapacity) {
        return batteryValidator.validateWattCapacityParameters(minWattCapacity, maxWattCapacity)
            .then(Mono.defer(() -> batteryRepository.findBatteriesByPostcodeRangeAndWattCapacity(
                startPostcode, endPostcode, minWattCapacity, maxWattCapacity)
                .collectList()))
            .map(batteries -> {
                List<String> names = batteries.stream()
                    .map(Battery::name)
                    .sorted()
                    .toList();
                    
                double totalCapacity = batteries.stream()
                    .mapToDouble(Battery::wattCapacity)
                    .sum();
                    
                double avgCapacity = batteries.isEmpty() ? 0 : 
                    totalCapacity / batteries.size();
                    
                return new BatteryResponse(names, totalCapacity, avgCapacity);
            });
    }
}
