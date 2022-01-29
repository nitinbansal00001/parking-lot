package com.intuit.parkingLot.service.impl;

import com.intuit.parkingLot.dto.enums.TicketStatus;
import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.response.ParkingAmountResponse;
import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import com.intuit.parkingLot.repo.nativeRepo.ParkingTicketRepo;
import com.intuit.parkingLot.repo.nativeRepo.PricingEntityRepo;
import com.intuit.parkingLot.service.TicketGenerationService;
import com.intuit.parkingLot.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TicketGenerationServiceImpl implements TicketGenerationService {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    protected ParkingTicketRepo parkingTicketRepo;

    @Autowired
    protected PricingEntityRepo pricingEntityRepo;

    @Override
    public ParkingTicket generateParkingTicket(List<Long> spotIds, VehicleType vehicleType, String vehicleRegNo) {
        logger.info("Generating parking ticket");
        StringBuilder builder = new StringBuilder().append(spotIds.get(0));
        spotIds.remove(0);
        spotIds.forEach(val -> builder.append(",").append(val));
        String commaSeparatedSpots = builder.toString();

        ParkingTicketEntity parkingTicketEntity = ParkingTicketEntity.builder()
                .spotIds(commaSeparatedSpots)
                .vehicleType(vehicleType.toString())
                .vehicleRegNo(vehicleRegNo)
                .ticketStatus(TicketStatus.ACTIVE.toString())
                .entryTime(new Date())
                .build();

        parkingTicketRepo.save(parkingTicketEntity);
        logger.info("Parking ticket saved in DB");

        ParkingTicket parkingTicket = CommonUtils.getTicket(parkingTicketEntity);
        return parkingTicket;
    }

    @Override
    public ParkingAmountResponse generatePaymentResponse(ParkingTicketEntity parkingTicketEntity) {
        Date entryTime = parkingTicketEntity.getEntryTime();
        Date currentTime = new Date();

        long duration = currentTime.getTime() - entryTime.getTime();
        long durationInHours = TimeUnit.MILLISECONDS.toHours(duration);
        durationInHours += (TimeUnit.MILLISECONDS.toMinutes(duration) > 0) ? 1 : (TimeUnit.MILLISECONDS.toSeconds(duration)>0 ? 1 : 0);

        Double pricePerHour = pricingEntityRepo.findPerHourPriceForVehicleType(parkingTicketEntity.getVehicleType());

        Double totalAmount = pricePerHour * durationInHours;

        return ParkingAmountResponse.builder()
                .amount(totalAmount)
                .hours((int)durationInHours)
                .build();
    }
}
