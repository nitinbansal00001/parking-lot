package com.intuit.parkingLot.strategies;

import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.exceptions.NoParkingSpotAvailableException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;

public interface ParkingStrategy {
    public ParkingTicket reserveSpotForVehicle( String registrationNumber, Long parkingLotId) throws NoParkingSpotAvailableException, ParkingLotDoesNotExistException;
    public ParkingAmountResponse releaseSpot(ParkingTicketEntity parkingTicketEntity) throws InvalidParkingTicketException;
}
