package com.intuit.parkingLot.strategies.factory;

import com.intuit.parkingLot.dto.enums.VehicleType;
import com.intuit.parkingLot.strategies.ParkingStrategy;
import com.intuit.parkingLot.strategies.impl.BikeParkingStrategy;
import com.intuit.parkingLot.strategies.impl.BusParkingStrategy;
import com.intuit.parkingLot.strategies.impl.CarParkingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ParkingStrategyFactory {

    @Autowired
    private BikeParkingStrategy bikeParkingStrategy;

    @Autowired
    private CarParkingStrategy carParkingStrategy;

    @Autowired
    private BusParkingStrategy busParkingStrategy;

    public Map<VehicleType, ParkingStrategy> vehicleTypeParkingStrategyMap;

    @PostConstruct
    public void init() {
        vehicleTypeParkingStrategyMap = new HashMap<VehicleType, ParkingStrategy>() {{
            put(VehicleType.BIKE, bikeParkingStrategy);
            put(VehicleType.CAR, carParkingStrategy);
            put(VehicleType.BUS, busParkingStrategy);
        }};
    }

    public ParkingStrategy getParkingStrategyForVehicle(VehicleType vehicleType) {
        if (!vehicleTypeParkingStrategyMap.containsKey(vehicleType))
            throw new IllegalArgumentException("No strategy present for give vehicle type");

        return vehicleTypeParkingStrategyMap.get(vehicleType);
    }

    public ParkingStrategy getParkingStrategyForVehicleType(String type) {
        VehicleType vehicleType = null;
        if (type.equalsIgnoreCase(VehicleType.BIKE.toString()))
            vehicleType = VehicleType.BIKE;
        else if (type.equalsIgnoreCase(VehicleType.CAR.toString()))
            vehicleType = VehicleType.CAR;
        else if (type.equalsIgnoreCase(VehicleType.BUS.toString()))
            vehicleType = VehicleType.BUS;

        return vehicleTypeParkingStrategyMap.get(vehicleType);
    }
}
