package com.challenge.vpp.dto;

import java.util.List;

public record BatteryResponse(
    List<String> names,
    double totalWattCapacity,
    double averageWattCapacity
) {}
