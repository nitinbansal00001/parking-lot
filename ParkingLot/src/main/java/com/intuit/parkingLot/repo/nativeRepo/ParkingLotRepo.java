package com.intuit.parkingLot.repo.nativeRepo;

import com.intuit.parkingLot.entities.ParkingLotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepo extends JpaRepository<ParkingLotEntity, Long> {
}
