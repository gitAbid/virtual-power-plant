package com.challenge.vpp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("batteries")
public record Battery(
    @Id
    Long id,
    String name,
    Integer postcode,
    Double wattCapacity
) {
    public static Battery from(String name, Integer postcode, Double wattCapacity) {
        return new Battery(null, name, postcode, wattCapacity);
    }
}
