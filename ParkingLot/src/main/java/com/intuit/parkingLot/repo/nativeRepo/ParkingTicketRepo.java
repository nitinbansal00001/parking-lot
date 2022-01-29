package com.intuit.parkingLot.repo.nativeRepo;

import com.intuit.parkingLot.entities.ParkingTicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingTicketRepo extends JpaRepository<ParkingTicketEntity, Long> {

}
