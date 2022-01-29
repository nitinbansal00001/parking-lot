package com.intuit.parkingLot.service;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.request.ParkingSpotObject;
import com.intuit.parkingLot.exceptions.InvalidParkingSpotException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.exceptions.ParkingLotException;
import com.intuit.parkingLot.exceptions.ParkingSpotAlreadyExistsException;

public interface AdminService {
    public void createParkingLot();
    public void addNewSpot(ParkingSpotObject parkingSpotObject, Long parkingLotId) throws ParkingLotDoesNotExistException, ParkingSpotAlreadyExistsException;
    public void addPriceForVehicleType(VehicleType vehicleType, Double price);
    void modifyAvailabilityOfSpot(Long spotId, Boolean operational) throws InvalidParkingSpotException, ParkingLotException;
}
