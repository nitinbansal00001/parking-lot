package com.intuit.parkingLot.dto.request;

import com.intuit.parkingLot.dto.enums.SpotType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpot {
    private SpotType spotType;
    private Integer level;
    private Integer row;
    private Integer col;
    private Boolean empty;
    private Boolean operational;
}
