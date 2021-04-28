package com.example.tellenceparking.requests;

import com.example.tellenceparking.model.ParkingLot;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/parking-spaces-status")
    Call<ParkingLot> getParkingStatus();
}
