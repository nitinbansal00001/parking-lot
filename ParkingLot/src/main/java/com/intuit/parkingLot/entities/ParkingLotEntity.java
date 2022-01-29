package com.intuit.parkingLot.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity @Data @Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "parking_lot_entity")
public class ParkingLotEntity {
    @Id
    @GeneratedValue
    @Column(name = "parking_lot_id")
    private Long parkingLotId;

    @Column(name = "location")
    private String location;

    @Column(name = "min_floor")
    private Integer minFloor;

    @Column(name = "max_floor")
    private Integer maxFloor;

    @OneToMany(mappedBy = "parkingLotFK")
    private Set<ParkingSpotEntity> spotEntities;

    @OneToMany(mappedBy = "parkingLotPricingEntityFK")
    private Set<PricingEntity> pricingEntities;

    @OneToMany(mappedBy = "parkingLotTicketEntityFK")
    private Set<ParkingTicketEntity> parkingTicketEntities;
}
