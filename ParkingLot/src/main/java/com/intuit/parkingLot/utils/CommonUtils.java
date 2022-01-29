package com.intuit.parkingLot.utils;

import com.intuit.parkingLot.dto.response.ParkingTicket;
import com.intuit.parkingLot.entities.ParkingLotEntity;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import com.intuit.parkingLot.dto.request.ParkingSpot;
import com.intuit.parkingLot.entities.ParkingTicketEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommonUtils {

    private static final Logger logger = LogManager.getLogger();

    public static ParkingSpotEntity createNewSpotEntity(ParkingSpot parkingSpot, ParkingLotEntity parkingLotEntity) {
        final String spotNumber = new StringBuilder().append(parkingSpot.getLevel()).append(parkingSpot.getRow()).append(parkingSpot.getCol()).toString();

        logger.info("creating spot entity for adding new spot");
        ParkingSpotEntity parkingSpotEntity = ParkingSpotEntity.builder()
                .spotType(parkingSpot.getSpotType().toString())
                .lineNumber(parkingSpot.getRow())
                .position(parkingSpot.getCol())
                .spotNumber(spotNumber)
                .level(parkingSpot.getLevel())
                .empty(Boolean.TRUE)
                .operational(Boolean.TRUE)
                .parkingLotFK(parkingLotEntity)
                .build();

        return parkingSpotEntity;
    }

    public static ParkingTicket getTicket(ParkingTicketEntity parkingTicketEntity) {
        ParkingTicket parkingTicket = ParkingTicket.builder()
                .ticketId(parkingTicketEntity.getTicketId())
                .spotIds(parkingTicketEntity.getSpotIds())
                .vehicleType(parkingTicketEntity.getVehicleType())
                .vehicleRegNo(parkingTicketEntity.getVehicleRegNo())
                .entryTime(parkingTicketEntity.getEntryTime())
                .build();

        return parkingTicket;
    }

    public static Double calculateAmount(Double pricePerHour, Date entryTime, Date exitTime) {
        long duration = exitTime.getTime() - entryTime.getTime();
        long durationInHours = TimeUnit.MILLISECONDS.toHours(duration);
        durationInHours += (TimeUnit.MILLISECONDS.toMinutes(duration) > 0) ? 1 : (TimeUnit.MILLISECONDS.toSeconds(duration)>0 ? 1 : 0);

        return durationInHours*pricePerHour;
    }
}
