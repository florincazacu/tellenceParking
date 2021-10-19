package com.example.tellenceparking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tellenceparking.geofence.NotificationService;
import com.example.tellenceparking.layout.Floor;
import com.example.tellenceparking.layout.ParkingSlotStatus;
import com.example.tellenceparking.model.ParkingLot;
import com.example.tellenceparking.model.ParkingSpace;
import com.example.tellenceparking.permissions.LocationPermissionManager;
import com.example.tellenceparking.requests.ParkingSlotsRequest;
import com.example.tellenceparking.vector.VectorChildFinder;
import com.example.tellenceparking.vector.VectorDrawableCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final ParkingSlotsRequest parkingSlotsRequest = new ParkingSlotsRequest();

    private LocationPermissionManager locationPermissionManager;
    private VectorChildFinder vector;
    private ImageView floor_1;
    private ImageView floor_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floor_1 = findViewById(R.id.floor1);
        floor_2 = findViewById(R.id.floor2);
        vector = new VectorChildFinder(this, R.drawable.ic_underground1, floor_1);

        NotificationService.createChannel(this);
        locationPermissionManager = new LocationPermissionManager(this);


        parkingSlotsRequest.fetchParkingLot(new ParkingSlotsRequest.SlotRequestCallback() {
            @Override
            public void onSlotsReceived(ParkingLot parkingLot) {
                populateParkingLot(parkingLot.getParking_spaces());
            }

            @Override
            public void onSlotsFailure() {
                Log.e(TAG, "failed to get slots status");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        locationPermissionManager.checkPermissionsAndStartGeofencing();
    }

    /*
     *  When we get the result from asking the user to turn on device location, we call
     *  checkDeviceLocationSettingsAndStartGeofence again to make sure it's actually on, but
     *  we don't resolve the check to keep the user from seeing an endless loop.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        locationPermissionManager.onActivityResult(requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        locationPermissionManager.onRequestPermissionsResult(requestCode, grantResults);
    }

//    private void setUpRecyclerView() {
//        groupAdapter.setSpanCount(4);
//        RecyclerView rv = findViewById(R.id.recycler_view);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, groupAdapter.getSpanCount());
//        gridLayoutManager.setSpanSizeLookup(groupAdapter.getSpanSizeLookup());
//        rv.setLayoutManager(gridLayoutManager);
//        rv.setAdapter(groupAdapter);
//    }

    private void populateParkingLot(Map<String, List<ParkingSpace>> parkingSpaces) {
        parkingSpaces.forEach((floor, spaces) -> {
            Floor currentFloor = new Floor(floor);
            if (currentFloor.getTitle().equals("-1")) {
                vector = new VectorChildFinder(this, R.drawable.ic_underground1, floor_1);
            }
            if (currentFloor.getTitle().equals("-2")) {
                vector = new VectorChildFinder(this, R.drawable.ic_underground2, floor_2);
            }
            spaces.forEach(parkingSpace -> {


                        if (parkingSpace.getId().contains("_") && !parkingSpace.getId().endsWith("_")) {
                            parkingSpace.setId(parkingSpace.getId().substring(parkingSpace.getId().indexOf('_') + 1));
                        }
                        VectorDrawableCompat.VFullPath path = vector.findPathByName(parkingSpace.getId());

                        if (path == null) {
                            Log.e(TAG, "null path for id: " + parkingSpace.getId());
                        } else {
                            if (parkingSpace.getStatus() == ParkingSlotStatus.FREE) {
                                path.setFillColor(Color.GREEN);
                            } else {
                                path.setFillColor(Color.RED);
                            }
                        }
                    }
            );
        });
    }
}