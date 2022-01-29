package com.intuit.parkingLot.dto.enums;

public enum SpotType {
    MOTORCYCLE, COMPACT, LARGE;

    public static SpotType getSpotTypeFromString(String spotType) {
        if (spotType.equalsIgnoreCase(MOTORCYCLE.toString()))
            return MOTORCYCLE;
        else if (spotType.equalsIgnoreCase(COMPACT.toString()))
            return COMPACT;
        else if (spotType.equalsIgnoreCase(LARGE.toString()))
            return LARGE;

        return null;
    }
}
