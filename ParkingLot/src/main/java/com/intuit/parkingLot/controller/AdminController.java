package com.intuit.parkingLot.controller;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.request.ParkingSpotObject;
import com.intuit.parkingLot.dto.response.BaseResponse;
import com.intuit.parkingLot.exceptions.InvalidParkingSpotException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.exceptions.ParkingLotException;
import com.intuit.parkingLot.exceptions.ParkingSpotAlreadyExistsException;
import com.intuit.parkingLot.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/createParkingLot")
    public Object createParkingLot(@RequestParam String location,
                                   @RequestParam Integer minLevel,
                                   @RequestParam Integer maxLevel) {
        adminService.createParkingLot(location, minLevel, maxLevel);
        return BaseResponse.buildSuccess("parking lot created");
    }

    @PostMapping("/addNewSlot")
    public Object addNewSlot(@RequestBody ParkingSpotObject parkingSpotObject,
                           @RequestParam Long parkingLotId) throws ParkingLotDoesNotExistException, ParkingSpotAlreadyExistsException, InvalidParkingSpotException {
        adminService.addNewSpot(parkingSpotObject, parkingLotId);
        return BaseResponse.buildSuccess("parking spot created");
    }

    @PostMapping("/modifySpot")
    public Object modifySpotAvailability(@RequestParam Long spotId,
                                         @RequestParam Boolean operational) throws InvalidParkingSpotException, ParkingLotException {
        adminService.modifyAvailabilityOfSpot(spotId, operational);
        return BaseResponse.buildSuccess("parking spot created");
    }

    @PostMapping("/addPriceForVehicle")
    public Object addNewSlot(@RequestParam VehicleType vehicleType,
                            @RequestParam Double price,
                            @RequestParam Long parkingLotId) throws ParkingLotDoesNotExistException {
        adminService.addPriceForVehicleType(vehicleType, price, parkingLotId);
        return BaseResponse.buildSuccess("Parking Price added for vehicle");
    }

    @GetMapping("/getParkingPlan")
    public Object getParkingPlan(@RequestParam Long parkingLotId) {
        return BaseResponse.buildSuccess("parking plan query successful", adminService.getAllParkingSpots(parkingLotId));
    }

}
