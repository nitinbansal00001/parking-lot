package com.intuit.parkingLot.service.impl;

import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingTicketCriteriaRepo;
import com.intuit.parkingLot.service.ParkingValidationService;
import com.intuit.parkingLot.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class ParkingValidationServiceImpl implements ParkingValidationService {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    protected ParkingTicketCriteriaRepo parkingTicketCriteriaRepo;

    @Override
    public ParkingTicket validateIfVehicleAlreadyParked(String vehicleRegNo) {
        List<ParkingTicketEntity> parkingTicketEntities = parkingTicketCriteriaRepo.getActiveTicketForRegistrationNumber(vehicleRegNo);

        if (!CollectionUtils.isEmpty(parkingTicketEntities)) {
            logger.info("Already active ticket exists for vehicle with registration number {{}}", vehicleRegNo);
            return CommonUtils.getTicket(parkingTicketEntities.get(0));
        }
        return null;
    }
}
