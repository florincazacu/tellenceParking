package com.example.tellenceparking.requests;

import android.util.Log;

import com.example.tellenceparking.model.ParkingLot;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;

public class ParkingSlotsRequest {

    GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);

    public void fetchParkingLot(SlotRequestCallback callback) {
        Call<ParkingLot> call = service.getParkingStatus();
        call.enqueue(new Callback<ParkingLot>() {
            @Override
            public void onResponse(@NotNull Call<ParkingLot> call, @NotNull Response<ParkingLot> response) {
                if (response.isSuccessful()) {
                    callback.onSlotsReceived(response.body());
                } else {
                    callback.onSlotsFailure();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ParkingLot> call, @NotNull Throwable t) {
                Log.e("call error: ", t.toString());
                callback.onSlotsFailure();
            }
        });
    }

    public interface SlotRequestCallback {
        void onSlotsReceived(ParkingLot parkingLot);

        void onSlotsFailure();
    }

    public interface GetDataService {
        @GET("/parking-spaces-status")
        Call<ParkingLot> getParkingStatus();
    }
}


