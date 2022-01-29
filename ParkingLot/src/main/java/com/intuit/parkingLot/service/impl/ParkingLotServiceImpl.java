package com.intuit.parkingLot.service.impl;

import com.intuit.parkingLot.dto.enums.TicketStatus;
import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingTicketException;
import com.intuit.parkingLot.exceptions.NoParkingSpotAvailableException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingTicketCriteriaRepo;
import com.intuit.parkingLot.repo.nativeRepo.ParkingTicketRepo;
import com.intuit.parkingLot.service.ParkingLotService;
import com.intuit.parkingLot.strategies.ParkingStrategy;
import com.intuit.parkingLot.strategies.factory.ParkingStrategyFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    private ParkingStrategyFactory parkingStrategyFactory;

    @Autowired
    private ParkingTicketRepo parkingTicketRepo;

    @Autowired
    private ParkingTicketCriteriaRepo parkingTicketCriteriaRepo;

    @Override
    public ParkingTicket parkVehicle(VehicleType vehicleType, String registrationNumber, Long parkingLotId) throws NoParkingSpotAvailableException, ParkingLotDoesNotExistException {
        ParkingStrategy parkingStrategy = parkingStrategyFactory.getParkingStrategyForVehicle(vehicleType);

        logger.info("Getting emply slot for Vehicle type : {{}} with registration number : {{}}", vehicleType, registrationNumber);
        return parkingStrategy.reserveSpotForVehicle(registrationNumber, parkingLotId);

    }

    @Override
    public ParkingAmountResponse releaseVehicle(String ticketId) throws InvalidParkingTicketException {
        logger.info("Releasing vehicle for ticket id : {}", ticketId);
        List<ParkingTicketEntity> parkingTicketEntityList = parkingTicketCriteriaRepo.getByTicketId(Long.parseLong(ticketId));
        ParkingTicketEntity parkingTicketEntity = parkingTicketEntityList.get(0);
        if (CollectionUtils.isEmpty(parkingTicketEntityList) || !parkingTicketEntity.getTicketStatus().equals(TicketStatus.ACTIVE.toString())) {
            logger.info("No such parking ticket");
            throw new InvalidParkingTicketException("No such parking ticket");
        }

//        logger.info("parkingTicketEntity : {}", parkingTicketEntity);

        ParkingStrategy parkingStrategy = parkingStrategyFactory.getParkingStrategyForVehicleType(parkingTicketEntity.getVehicleType());

        return parkingStrategy.releaseSpot(parkingTicketEntity);
    }
}
