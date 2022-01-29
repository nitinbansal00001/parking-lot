package com.intuit.parkingLot.service.impl;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.dto.request.ParkingSpot;
import com.intuit.parkingLot.dto.request.ParkingSpotObject;
import com.intuit.parkingLot.entities.ParkingLotEntity;
import com.intuit.parkingLot.entities.ParkingSpotEntity;
import com.intuit.parkingLot.entities.PricingEntity;
import com.intuit.parkingLot.exceptions.InvalidParkingSpotException;
import com.intuit.parkingLot.exceptions.ParkingLotDoesNotExistException;
import com.intuit.parkingLot.exceptions.ParkingLotException;
import com.intuit.parkingLot.exceptions.ParkingSpotAlreadyExistsException;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingLotCriteriaRepo;
import com.intuit.parkingLot.repo.criteriaRepo.ParkingSpotCriteriaRepo;
import com.intuit.parkingLot.repo.nativeRepo.ParkingLotRepo;
import com.intuit.parkingLot.repo.nativeRepo.ParkingSpotRepo;
import com.intuit.parkingLot.repo.nativeRepo.PricingEntityRepo;
import com.intuit.parkingLot.service.AdminService;
import com.intuit.parkingLot.utils.CommonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    protected ParkingSpotRepo parkingSpotRepo;

    @Autowired
    protected ParkingSpotCriteriaRepo parkingSpotCriteriaRepo;

    @Autowired
    protected PricingEntityRepo pricingEntityRepo;

    @Autowired
    protected ParkingLotRepo parkingLotRepo;

    @Autowired
    protected ParkingLotCriteriaRepo parkingLotCriteriaRepo;

    @Override
    public void createParkingLot(String location, Integer minLevel, Integer maxLevel) {
        ParkingLotEntity parkingLotEntity = ParkingLotEntity.builder()
                .location(location)
                .minFloor(minLevel)
                .maxFloor(maxLevel)
                .build();

        parkingLotRepo.save(parkingLotEntity);
        logger.info("Parking Lot created successfuly");
    }

    @Override
    public void addNewSpot(ParkingSpotObject parkingSpotObject, Long parkingLotId) throws ParkingLotDoesNotExistException, ParkingSpotAlreadyExistsException, InvalidParkingSpotException {
        ParkingLotEntity parkingLotEntity = parkingLotRepo.getById(parkingLotId);

        /** Check for valid Parking Lot */
        if (parkingLotEntity == null) {
            throw new ParkingLotDoesNotExistException("Parking lot does not exist");
        }

        for (ParkingSpot parkingSpot : parkingSpotObject.getParkingSpots()) {
            /** Check for valid floor */
            if (parkingSpot.getLevel()<parkingLotEntity.getMinFloor() || parkingSpot.getLevel()>parkingLotEntity.getMaxFloor()) {
                logger.info("Trying to insert on invalid floor, so can't insert");
                throw new InvalidParkingSpotException("Invalid parking spot");
            }

            final String spotNumber = new StringBuilder().append(parkingSpot.getLevel()).append(parkingSpot.getRow()).append(parkingSpot.getCol()).toString();
            List<ParkingSpotEntity> parkingSpotEntityList = parkingSpotRepo.getExistingSlotFromSpotNumber(spotNumber);
            if (!CollectionUtils.isEmpty(parkingSpotEntityList)) {
                logger.info("Trying to insert duplicate spot, so can't insert");
                continue;
//            throw new ParkingSpotAlreadyExistsException("Parking spot already exists at this location");
            }

            logger.info("Adding new spot : {}", parkingSpot);
            ParkingSpotEntity parkingSpotEntity = CommonUtils.createNewSpotEntity(parkingSpot, parkingLotEntity);

            parkingSpotRepo.save(parkingSpotEntity);
            logger.info("Parking spot added successful");
        }
    }

    @Override
    public void addPriceForVehicleType(VehicleType vehicleType, Double price, Long parkingLotId) throws ParkingLotDoesNotExistException {
        logger.info("updating price for vehicle type : {}, parking lot id : {}, price : {}", vehicleType, parkingLotId, price);
        ParkingLotEntity parkingLotEntity = parkingLotCriteriaRepo.getByParkingLotId(parkingLotId);

        logger.info("parkingLotEntity : {}", parkingLotEntity);
        /** Check for valid Parking Lot */
        if (parkingLotEntity == null) {
            throw new ParkingLotDoesNotExistException("Parking lot does not exist");
        }
        PricingEntity pricingEntity = PricingEntity.builder()
                .vehicleType(vehicleType.toString())
                .amountChangedPerHour(price)
                .parkingLotPricingEntityFK(parkingLotEntity)
                .build();

        pricingEntityRepo.save(pricingEntity);
        logger.info("price update successful");
    }

    @Override
    public void modifyAvailabilityOfSpot(Long spotId, Boolean operational) throws  ParkingLotException {
        int result = parkingSpotCriteriaRepo.changeSpotAvailability(spotId, operational);
        if (result != 1) {
            logger.info("Could not change spot availability");
            throw new ParkingLotException("Could not change spot availability");
        }
    }

    @Override
    public List<ParkingSpot> getAllParkingSpots(Long parkingLotId) {
        List<ParkingSpotEntity> parkingSpotEntityList = parkingSpotRepo.getAllSpotsForParkingLot(parkingLotId);
        List<ParkingSpot> parkingSpots = new ArrayList();
        parkingSpotEntityList.forEach(parkingSpotEntity -> parkingSpots.add(CommonUtils.createParkingSpotFromParkingSpotEntity(parkingSpotEntity)));
        Collections.sort(parkingSpots, (o1, o2) -> {
            if (o1.getLevel() == o2.getLevel()) {
                if (o1.getRow() == o2.getRow()) {
                    return o1.getCol()-o2.getCol();
                }
                return o1.getRow()-o2.getRow();
            }
            return o1.getLevel()-o2.getLevel();
        });
        return parkingSpots;
    }

}
