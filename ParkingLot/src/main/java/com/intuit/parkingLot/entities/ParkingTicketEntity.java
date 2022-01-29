package com.intuit.parkingLot.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity @Builder @NoArgsConstructor @AllArgsConstructor @Getter @Setter
@Table(
        indexes = {
                @Index(columnList = "vehicle_reg_no"),
                @Index(columnList = "ticket_status")
        }
)
public class ParkingTicketEntity {
    @Id
    @GeneratedValue
    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "spot_ids")
    private String spotIds; /** comma separated SpotIds */

    @Column(name = "ticket_status")
    private String ticketStatus;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "vehicle_reg_no")
    private String vehicleRegNo;

//    @Column(name = "entry_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "entry_time")
    private Date entryTime;

    @Column(name = "exit_time")
    private Date exitTime;


    @Column(name = "amount")
    private Double amount;

    @ManyToOne(targetEntity = ParkingLotEntity.class)
    private ParkingLotEntity parkingLotTicketEntityFK;
}
