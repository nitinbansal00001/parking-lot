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
import com.intuit.parkingLot.repo.nativeRepo.ParkingSpotRepo;
import com.intuit.parkingLot.repo.nativeRepo.PricingEntityRepo;
import com.intuit.parkingLot.service.ParkingValidationService;
import com.intuit.parkingLot.service.TicketGenerationService;
import com.intuit.parkingLot.strategies.ParkingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusParkingStrategy implements ParkingStrategy {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    protected ParkingLotRepo parkingLotRepo;

    @Autowired
    protected ParkingSpotRepo parkingSpotRepo;

    @Autowired
    protected ParkingSpotCriteriaRepo parkingSpotCriteriaRepo;

    @Autowired
    private TicketGenerationService ticketGenerationService;

    @Autowired
    protected ParkingValidationService parkingValidationService;

    @Autowired
    protected ParkingTicketCriteriaRepo parkingTicketCriteriaRepo;

    @Autowired
    protected PricingEntityRepo pricingEntityRepo;

    @Override
    public ParkingTicket reserveSpotForVehicle(String vehicleRegNo, Long parkingLotId) throws ParkingLotDoesNotExistException {

        ParkingTicket parkingTicket = parkingValidationService.validateIfVehicleAlreadyParked(vehicleRegNo);
        if (parkingTicket != null) {
            return parkingTicket;
        }

        List<ParkingSpotEntity> parkingSpotEntityList = null;
        ParkingLotEntity parkingLotEntity = parkingLotRepo.getById(parkingLotId);
        if (parkingLotEntity == null) {
            throw new ParkingLotDoesNotExistException("No such parking lot exists");
        }

        Integer minFloor = parkingLotEntity.getMinFloor();
        Integer maxFloor = parkingLotEntity.getMaxFloor();
        logger.info("min floor {{}}, max floor {{}}", minFloor, maxFloor);
        List<ParkingSpotEntity> foundEmptySpotsForBus = null;

        for (int floor = minFloor; floor <= maxFloor; floor++) {
            logger.info("Query for floor : {}", floor);
            parkingSpotEntityList = parkingSpotCriteriaRepo.getEmptySpotsForSpotTypeAndLevel(SpotType.LARGE, new Integer(floor));
            foundEmptySpotsForBus = getParkingSpotsForBus(parkingSpotEntityList);
            if (!CollectionUtils.isEmpty(foundEmptySpotsForBus)) {
                break;
            }
        }

        if (CollectionUtils.isEmpty(foundEmptySpotsForBus)) {
            logger.info("Parking is full");
            throw new RuntimeException("Parking is full");
        }
        logger.info("Parking Spots found for BUS, number of spots are : {}", foundEmptySpotsForBus.size());

        parkingTicket = finaliseReservation(foundEmptySpotsForBus, vehicleRegNo, parkingLotEntity);

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
        ParkingTicket parkingTicket = parkingSpotCriteriaRepo.updateSpotAndGetTicket(spotIds, VehicleType.BUS, vehicleRegNo, parkingLotEntity);
        return parkingTicket;
    }

    /**===============================================Private methods==============================================*/

    private List<ParkingSpotEntity> getParkingSpotsForBus(List<ParkingSpotEntity> parkingSpotEntityList) {
        parkingSpotEntityList = parkingSpotEntityList.stream().sorted((o1, o2) ->
                (o1.getLineNumber() == o2.getLineNumber()) ? (o1.getPosition() - o2.getPosition()) : (o1.getLineNumber() - o2.getLineNumber())
        ).collect(Collectors.toList());

        int start=0, end=4;

        while (end < parkingSpotEntityList.size()) {
            ParkingSpotEntity p1 = parkingSpotEntityList.get(start);
            ParkingSpotEntity p2 = parkingSpotEntityList.get(end);

            if (p1.getLineNumber()==p2.getLineNumber() && Math.abs(p1.getPosition()- p2.getPosition())==4) {
                logger.info("Found 5 continuous large spots");
                return parkingSpotEntityList.subList(start, end+1);
            } else {
                start++;
                end++;
            }
        }
        return null;
    }
}
