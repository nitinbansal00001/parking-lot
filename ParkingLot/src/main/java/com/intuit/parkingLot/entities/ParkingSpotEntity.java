package com.intuit.parkingLot.entities;

import com.intuit.parkingLot.dto.enums.SpotType;
import lombok.*;

import javax.persistence.*;

@Entity @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
@Table(
        name = "parking_spot_entity",
        indexes = {
                @Index(columnList = "is_empty"),
                @Index(columnList = "operational"),
                @Index(columnList = "spot_type"),
                @Index(columnList = "level")
        }
)
public class ParkingSpotEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @ManyToOne(targetEntity = ParkingLotEntity.class)
    private ParkingLotEntity parkingLotFK;

    @Column(name = "spot_type")
    private String spotType;

    @Column(name = "level")
    private Integer level;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(name = "position")
    private Integer position;

    @Column(name = "spot_number")
    private String spotNumber;

    @Column(name = "is_empty")
    private Boolean empty;

    @Column(name = "operational")
    private Boolean operational;

    public Boolean getMotorcycleSpots() {
        return this.spotType.equalsIgnoreCase(SpotType.MOTORCYCLE.toString());
    }

    public Boolean getCompactSpots() {
        return this.spotType.equalsIgnoreCase(SpotType.COMPACT.toString());
    }

    public Boolean getLargeSpots() {
        return this.spotType.equalsIgnoreCase(SpotType.LARGE.toString());
    }
}
