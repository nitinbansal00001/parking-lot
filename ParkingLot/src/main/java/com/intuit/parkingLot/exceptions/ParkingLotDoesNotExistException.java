package com.intuit.parkingLot.exceptions;

public class ParkingLotDoesNotExistException extends Exception {
    public ParkingLotDoesNotExistException(String msg) {
        super(msg);
    }
}
