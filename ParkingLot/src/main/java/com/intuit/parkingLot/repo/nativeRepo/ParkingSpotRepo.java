package com.intuit.parkingLot.repo.nativeRepo;

import com.intuit.parkingLot.entities.ParkingSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ParkingSpotRepo extends JpaRepository<ParkingSpotEntity, Long> {

    @Query("select p from ParkingSpotEntity p where p.spotNumber = :spotNumber")
    public List<ParkingSpotEntity> getExistingSlotFromSpotNumber(String spotNumber);

    @Query("select p from ParkingSpotEntity p where p.spotType= :spotType and p.operational= :operational and p.empty= :empty")
    List<ParkingSpotEntity> getAllSpotsForSpotType(String spotType, Boolean empty, Boolean operational);

    @Query("select p from ParkingSpotEntity p where p.spotType= :spotType and p.operational= :operational and p.empty= :empty and p.level= :level")
    List<ParkingSpotEntity> getAllSpotsForSpotTypeAndLevel(String spotType, Boolean empty, Boolean operational, Integer level);

    @Query("select p from ParkingSpotEntity p where p.parkingLotFK.parkingLotId= :parkingLotId")
    List<ParkingSpotEntity> getAllSpotsForParkingLot(Long parkingLotId);
}
