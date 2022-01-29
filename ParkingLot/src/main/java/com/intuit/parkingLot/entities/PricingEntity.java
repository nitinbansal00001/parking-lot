package com.intuit.parkingLot.entities;

import lombok.*;

import javax.persistence.*;

@Entity @Builder @NoArgsConstructor @AllArgsConstructor @Getter @Setter
@Table(name = "pricing_entity")
public class PricingEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name= "amount_charged_per_hour")
    private Double amountChangedPerHour;
}
