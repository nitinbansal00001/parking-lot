package com.intuit.parkingLot.service;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.exceptions.NoParkingSpotAvailableException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;

public interface ParkingLotService {
    public ParkingTicket parkVehicle(VehicleType vehicleType, String registrationNumber, Long parkingLotId) throws NoParkingSpotAvailableException, ParkingLotDoesNotExistException;
    public ParkingAmountResponse releaseVehicle(String ticketId) throws InvalidParkingTicketException;
}
