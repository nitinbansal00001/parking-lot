package com.intuit.parkingLot.controller;

import com.intuit.parkingLot.dto.response.BaseResponse;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.exceptions.NoParkingSpotAvailableException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.service.ParkingLotService;
import com.intuit.parkingLot.dto.enums.VehicleType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parkingLot")
public class ParkingController extends BaseController {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ParkingLotService parkingLotService;

    @PostMapping("/parkVehicle")
    public Object parkVehicle(@RequestParam VehicleType vehicleType,
                              @RequestParam String registrationNumber,
                              @RequestParam Long parkingLotId) throws NoParkingSpotAvailableException, ParkingLotDoesNotExistException {
        return BaseResponse.buildSuccess("Vehicle Parking successful" ,parkingLotService.parkVehicle(vehicleType, registrationNumber, parkingLotId));
    }

    @PostMapping("/releaseVehicle")
    public Object releaseVehicle(@RequestParam String ticketId) throws InvalidParkingTicketException {
        return BaseResponse.buildSuccess("Vehicle release successful", parkingLotService.releaseVehicle(ticketId));
    }
}
