package com.intuit.parkingLot.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLot {
    private String location;
    private Integer minLevel;
    private Integer maxLevel;
}
