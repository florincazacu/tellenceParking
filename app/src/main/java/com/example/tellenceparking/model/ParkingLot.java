package com.example.tellenceparking.model;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParkingLot {

    Map<String, List<ParkingSpace>> parking_spaces = new LinkedHashMap<>();

    public Map<String, List<ParkingSpace>> getParking_spaces() {
        return parking_spaces;
    }

    @NotNull
    @Override
    public String toString() {
        return "ParkingLot{" +
                "parking_spaces=" + parking_spaces +
                '}';
    }
}
