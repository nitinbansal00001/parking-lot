package com.intuit.parkingLot.service;

import com.intuit.parkingLot.dto.response.ParkingTicket;

public interface ParkingValidationService {
    public ParkingTicket validateIfVehicleAlreadyParked(String vehicleRegNo);
}
