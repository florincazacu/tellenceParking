package com.example.tellenceparking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tellenceparking.geofence.NotificationService;
import com.example.tellenceparking.layout.Floor;
import com.example.tellenceparking.layout.ParkingSpaceItem;
import com.example.tellenceparking.layout.ParkingSpaceItemView;
import com.example.tellenceparking.model.ParkingLot;
import com.example.tellenceparking.model.ParkingSpace;
import com.example.tellenceparking.permissions.LocationPermissionManager;
import com.example.tellenceparking.requests.ParkingSlotsRequest;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;
import com.xwray.groupie.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final ParkingSlotsRequest parkingSlotsRequest = new ParkingSlotsRequest();
    private final GroupAdapter<ViewHolder> groupAdapter = new GroupAdapter<>();

    private LocationPermissionManager locationPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpRecyclerView();
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
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, int[] grantResults) {
        locationPermissionManager.onRequestPermissionsResult(requestCode, grantResults);
    }

    //TODO
    private ParkingSpaceItemView generateParkingSpaceView(String id, int status) {
        return new ParkingSpaceItemView(new ParkingSpaceItem(id, status));
    }

    private void setUpRecyclerView() {
        groupAdapter.setSpanCount(4);
        RecyclerView rv = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, groupAdapter.getSpanCount());
        gridLayoutManager.setSpanSizeLookup(groupAdapter.getSpanSizeLookup());
        rv.setLayoutManager(gridLayoutManager);
        rv.setAdapter(groupAdapter);
    }

    private void populateParkingLot(Map<String, List<ParkingSpace>> parkingSpaces) {
        parkingSpaces.forEach((floor, spaces) -> {
            Floor currentFloor = new Floor(floor);
            spaces.forEach(parkingSpace -> currentFloor.getParkingSpaces()
                    .add(generateParkingSpaceView(parkingSpace.getId(), parkingSpace.getStatus())));
            Floor.Header floorGroup = new Floor.Header("Floor: " + floor);
            groupAdapter.add(new Section(floorGroup, currentFloor.getParkingSpaces()));
        });
    }
}