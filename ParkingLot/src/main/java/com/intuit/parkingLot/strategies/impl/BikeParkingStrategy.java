package com.intuit.parkingLot.strategies.impl;

import com.intuit.parkingLot.dto.enums.SpotType;
import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingLotEntity;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingSpotCriteriaRepo;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingTicketCriteriaRepo;
import com.intuit.parkingLot.repo.nativeRepo.ParkingLotRepo;
import com.intuit.parkingLot.service.ParkingValidationService;
import com.intuit.parkingLot.service.TicketGenerationService;
import com.intuit.parkingLot.strategies.ParkingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BikeParkingStrategy implements ParkingStrategy {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    protected ParkingLotRepo parkingLotRepo;

    @Autowired
    protected ParkingSpotCriteriaRepo parkingSpotCriteriaRepo;

    @Autowired
    protected ParkingValidationService parkingValidationService;

    @Autowired
    protected ParkingTicketCriteriaRepo parkingTicketCriteriaRepo;

    @Autowired
    protected TicketGenerationService ticketGenerationService;

    @Override
    public ParkingTicket reserveSpotForVehicle(String vehicleRegNo, Long parkingLotId) throws ParkingLotDoesNotExistException {

        ParkingTicket parkingTicket = parkingValidationService.validateIfVehicleAlreadyParked(vehicleRegNo);
        if (parkingTicket != null) {
            return parkingTicket;
        }

        ParkingSpotEntity parkingSpotEntity = null;

        ParkingLotEntity parkingLotEntity = parkingLotRepo.getById(parkingLotId);
        if (parkingLotEntity == null) {
            throw new ParkingLotDoesNotExistException("No such parking lot exists");
        }

        List<SpotType> spotTypes = Arrays.asList(SpotType.MOTORCYCLE, SpotType.COMPACT, SpotType.LARGE);
        for (SpotType spotType : spotTypes) {
            parkingSpotEntity = parkingSpotCriteriaRepo.getParkingSpotForSpotTypeOrderByLevel(spotType);

            if (parkingSpotEntity != null)
                break;
        }

        if (parkingSpotEntity == null) {
            logger.info("Parking is full");
            throw new RuntimeException("Parking is full");
        }

        logger.info("Parking Spot found : {}", parkingSpotEntity.toString());

        parkingTicket = finaliseReservation(Arrays.asList(parkingSpotEntity), vehicleRegNo);

        logger.info("Parking ticket is : {}", parkingTicket.toString());

        return parkingTicket;
    }

    @Override
    public ParkingAmountResponse releaseSpot(ParkingTicketEntity parkingTicketEntity) throws InvalidParkingTicketException {
        parkingTicketCriteriaRepo.markTicketPaidAndMakeSpotsFree(parkingTicketEntity);
        return ticketGenerationService.generatePaymentResponse(parkingTicketEntity);
    }

    public ParkingTicket finaliseReservation(List<ParkingSpotEntity> spotEntities, String vehicleRegNo) {
        logger.info("finalising reservation");
        List<Long> spotIds = spotEntities.stream().map(s -> s.getId()).collect(Collectors.toList());
        ParkingTicket parkingTicket = parkingSpotCriteriaRepo.updateSpotAndGetTicket(spotIds, VehicleType.BIKE, vehicleRegNo);
        return parkingTicket;
    }
}
