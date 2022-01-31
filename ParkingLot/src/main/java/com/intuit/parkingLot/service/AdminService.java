package com.intuit.parkingLot.service;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.request.ParkingSpot;
import com.intuit.parkingLot.dto.request.ParkingSpotObject;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingSpotException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.exceptions.ParkingLotException;
import com.intuit.parkingLot.exceptions.ParkingSpotAlreadyExistsException;

import java.util.List;

public interface AdminService {
    Long createParkingLot(String location, Integer minLevel, Integer maxLevel);
    void addNewSpot(ParkingSpotObject parkingSpotObject, Long parkingLotId) throws ParkingLotDoesNotExistException, ParkingSpotAlreadyExistsException, InvalidParkingSpotException;
    void addPriceForVehicleType(VehicleType vehicleType, Double price, Long parkingLotId) throws ParkingLotDoesNotExistException;
    List<ParkingSpot> getAllParkingSpots(Long parkingLotId);

    void modifyAvailabilityOfSpot(Integer level, Integer row, Integer col, Boolean operational) throws InvalidParkingSpotException, ParkingLotException;
}
