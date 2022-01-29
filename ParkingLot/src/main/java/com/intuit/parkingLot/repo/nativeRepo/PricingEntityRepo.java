package com.intuit.parkingLot.repo.nativeRepo;

import com.intuit.parkingLot.entities.PricingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PricingEntityRepo extends JpaRepository<PricingEntity, Long> {

    @Query("select p.amountChangedPerHour from PricingEntity p where p.vehicleType = :vehicleType")
    public Double findPerHourPriceForVehicleType(String vehicleType);
}
