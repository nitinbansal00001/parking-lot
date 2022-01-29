package com.intuit.parkingLot.exceptions;

public class ParkingSpotAlreadyExistsException extends Exception {
    public ParkingSpotAlreadyExistsException(String msg) {
        super(msg);
    }
}
