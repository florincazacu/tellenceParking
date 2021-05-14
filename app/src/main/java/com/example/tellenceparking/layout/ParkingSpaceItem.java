package com.example.tellenceparking.layout;

import com.example.tellenceparking.R;

public class ParkingSpaceItem {

    private final String id;
    private final int status;

    public ParkingSpaceItem(String id, int status) {
        this.id = id;
        this.status = status;
    }

    public int getTitleColor() {
        switch (status) {
            case ParkingSlotStatus.FREE:
                return R.color.green;
            case ParkingSlotStatus.UNKNOWN:
                return R.color.yellow;
            case ParkingSlotStatus.OCCUPIED:
            default:
                return R.color.red;
        }
    }

    public boolean displayCarImage() {
        return status == ParkingSlotStatus.OCCUPIED;
    }

    public String getTitle() {
        if (id.contains("_") && !id.endsWith("_")) {
            return id.substring(id.indexOf('_') + 1);
        }
        return id;
    }
}
