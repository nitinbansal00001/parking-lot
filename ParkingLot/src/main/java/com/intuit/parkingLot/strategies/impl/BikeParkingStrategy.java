package com.intuit.parkingLot.strategies.impl;

import com.intuit.parkingLot.dto.enums.SpotType;
import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingLotEntity;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.entities.PricingEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingSpotCriteriaRepo;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingTicketCriteriaRepo;
import com.intuit.parkingLot.repo.nativeRepo.ParkingLotRepo;
import com.intuit.parkingLot.repo.nativeRepo.ParkingTicketRepo;
import com.intuit.parkingLot.repo.nativeRepo.PricingEntityRepo;
import com.intuit.parkingLot.service.ParkingValidationService;
import com.intuit.parkingLot.service.TicketGenerationService;
import com.intuit.parkingLot.strategies.ParkingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
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
    protected ParkingTicketRepo parkingTicketRepo;

    @Autowired
    protected ParkingTicketCriteriaRepo parkingTicketCriteriaRepo;

    @Autowired
    protected TicketGenerationService ticketGenerationService;

    @Autowired
    protected PricingEntityRepo pricingEntityRepo;

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

        parkingTicket = finaliseReservation(Arrays.asList(parkingSpotEntity), vehicleRegNo, parkingLotEntity);

        logger.info("Parking ticket is : {}", parkingTicket.toString());

        return parkingTicket;
    }

    @Override
    public ParkingAmountResponse releaseSpot(ParkingTicketEntity parkingTicketEntity) throws InvalidParkingTicketException {
        Double amountPerHour = pricingEntityRepo.findPerHourPriceForVehicleType(parkingTicketEntity.getVehicleType(), parkingTicketEntity.getParkingLotTicketEntityFK().getParkingLotId());
        Date exitTime = new Date();
        parkingTicketCriteriaRepo.markTicketPaidAndMakeSpotsFree(parkingTicketEntity, amountPerHour, exitTime);
//        logger.info("updated ticket : {}", parkingTicketEntity);
//        ParkingTicketEntity updatedParkingTicketEntity = parkingTicketRepo.getById(parkingTicketEntity.getTicketId());
//        logger.info("amount : {}, exit time : {}", updatedParkingTicketEntity.getAmount(), updatedParkingTicketEntity.getExitTime());
        return ticketGenerationService.generatePaymentResponse1(parkingTicketEntity.getEntryTime(), exitTime, amountPerHour);
    }

    public ParkingTicket finaliseReservation(List<ParkingSpotEntity> spotEntities, String vehicleRegNo, ParkingLotEntity parkingLotEntity) {
        logger.info("finalising reservation");
        List<Long> spotIds = spotEntities.stream().map(s -> s.getId()).collect(Collectors.toList());
        ParkingTicket parkingTicket = parkingSpotCriteriaRepo.updateSpotAndGetTicket(spotIds, VehicleType.BIKE, vehicleRegNo, parkingLotEntity);
        return parkingTicket;
    }
}
