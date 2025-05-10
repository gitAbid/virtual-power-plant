package com.chellenge.vpp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("batteries")
public record Battery(
    @Id
    Long id,
    String name,
    String postcode,
    Double wattCapacity
) {
    public static Battery from(String name, String postcode, Double wattCapacity) {
        return new Battery(null, name, postcode, wattCapacity);
    }
}
