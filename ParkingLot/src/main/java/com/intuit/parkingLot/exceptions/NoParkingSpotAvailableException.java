package com.intuit.parkingLot.exceptions;

public class NoParkingSpotAvailableException extends Exception {
    public NoParkingSpotAvailableException(String msg) {
        super(msg);
    }
}
