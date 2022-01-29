package com.intuit.parkingLot.dto.response;

import lombok.*;

import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingTicket {
    private Long ticketId;
    private String spotIds;
    private String vehicleType;
    private String vehicleRegNo;
    private Date entryTime;
}
