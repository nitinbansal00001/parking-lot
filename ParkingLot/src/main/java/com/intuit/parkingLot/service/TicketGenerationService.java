package com.intuit.parkingLot.service;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingTicketEntity;

import java.util.List;

public interface TicketGenerationService {
    public ParkingTicket generateParkingTicket(List<Long> spotIds, VehicleType vehicleType, String vehicleRegNo);

    public ParkingAmountResponse generatePaymentResponse(ParkingTicketEntity parkingTicketEntity);
}
