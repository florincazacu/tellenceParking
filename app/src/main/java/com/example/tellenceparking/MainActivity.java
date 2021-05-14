package com.example.tellenceparking;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tellenceparking.geofence.GeofenceBroadcastReceiver;
import com.example.tellenceparking.geofence.GeofencingConstants;
import com.example.tellenceparking.geofence.LandmarkDataObject;
import com.example.tellenceparking.geofence.NotificationUtils;
import com.example.tellenceparking.layout.Floor;
import com.example.tellenceparking.layout.ParkingSpaceItem;
import com.example.tellenceparking.layout.ParkingSpaceItemView;
import com.example.tellenceparking.model.ParkingLot;
import com.example.tellenceparking.requests.ParkingSlotsRequest;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Section;
import com.xwray.groupie.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String ACTION_GEOFENCE_EVENT = "MainActivity.ACTION_GEOFENCE_EVENT";
    private static final int REQUEST_TURN_DEVICE_LOCATION_ON = 29;
    private static final int REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33;
    private static final int REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34;
    private static final int LOCATION_PERMISSION_INDEX = 0;
    private static final int BACKGROUND_LOCATION_PERMISSION_INDEX = 1;

    private final boolean runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private final ParkingSlotsRequest parkingSlotsRequest = new ParkingSlotsRequest();
    private final List<Geofence> geofences = new ArrayList<>();
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        geofencingClient = LocationServices.getGeofencingClient(this);
        NotificationUtils.createChannel(this);

        parkingSlotsRequest.fetchParkingLot(new ParkingSlotsRequest.SlotRequestCallback() {
            @Override
            public void onSlotsReceived(ParkingLot parkingLot) {
                populateView(parkingLot);
            }

            @Override
            public void onSlotsFailure() {
                Toast.makeText(MainActivity.this, "onSlotsFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ParkingSpaceItemView generateParkingSpaceView(String id, int status) {
        return new ParkingSpaceItemView(new ParkingSpaceItem(id, status));
    }

    private void populateView(ParkingLot parkingLot) {
        GroupAdapter<ViewHolder> groupAdapter = new GroupAdapter<>();
        groupAdapter.setSpanCount(4);

        parkingLot.getParking_spaces().forEach((floor, parkingSpaces) -> {
            Floor currentFloor = new Floor(floor);

            parkingSpaces.forEach(parkingSpace -> currentFloor.getParkingSpaces().add(generateParkingSpaceView(parkingSpace.getId(), parkingSpace.getStatus())));
            RecyclerView rv = findViewById(R.id.recycler_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, groupAdapter.getSpanCount());
            gridLayoutManager.setSpanSizeLookup(groupAdapter.getSpanSizeLookup());
            rv.setLayoutManager(gridLayoutManager);
            rv.setAdapter(groupAdapter);

            Floor.Header floorGroup = new Floor.Header("Floor: " + floor);
            groupAdapter.add(new Section(floorGroup, currentFloor.getParkingSpaces()));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        checkPermissionsAndStartGeofencing();
    }

    /*
     *  When we get the result from asking the user to turn on device location, we call
     *  checkDeviceLocationSettingsAndStartGeofence again to make sure it's actually on, but
     *  we don't resolve the check to keep the user from seeing an endless loop.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            // We don't rely on the result code, but just check the location setting again
            checkDeviceLocationSettingsAndStartGeofence(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
        if (grantResults.length == 0 || grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                        grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                                PackageManager.PERMISSION_DENIED)) {
            // Permission denied.
            Snackbar.make(findViewById(android.R.id.content), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.settings, view ->
                            startActivity(new Intent()
                                    .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null))
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)))
                    .show();

        } else {
            checkDeviceLocationSettingsAndStartGeofence(true);
        }
    }

    private void checkPermissionsAndStartGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence(true);
        } else {
            requestForegroundAndBackgroundLocationPermissions();
        }
    }

    /*
     *  Uses the Location Client to check the current state of location settings, and gives the user
     *  the opportunity to turn on location services within our app.
     */
    private void checkDeviceLocationSettingsAndStartGeofence(Boolean resolve) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest);

        locationSettingsResponseTask.addOnFailureListener((exception) -> {
            if (exception instanceof ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    ResolvableApiException e = (ResolvableApiException) exception;
                    e.startResolutionForResult(MainActivity.this,
                            REQUEST_TURN_DEVICE_LOCATION_ON);
                } catch (IntentSender.SendIntentException sendEx) {
                    Log.d(TAG, "Error getting location settings resolution: " + sendEx.getMessage());
                }
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.location_required_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, v -> checkDeviceLocationSettingsAndStartGeofence(true)).show();
            }
        });

        locationSettingsResponseTask.addOnCompleteListener((result) -> {
            if (result.isSuccessful()) {
                addGeofences();
            }
        });
    }

    /*
     *  Determines whether the app has the appropriate permissions across Android 10+ and all other
     *  Android versions.
     */
    @TargetApi(29)
    private boolean foregroundAndBackgroundLocationPermissionApproved() {
        boolean foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
        boolean backgroundPermissionApproved;
        if (runningQOrLater) {
            backgroundPermissionApproved = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        } else {
            backgroundPermissionApproved = true;
        }
        return foregroundLocationApproved && backgroundPermissionApproved;
    }

    /*
     *  Requests ACCESS_FINE_LOCATION and (on Android 10+ (Q) ACCESS_BACKGROUND_LOCATION.
     */
    @TargetApi(29)
    private void requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            return;
        }

        // Else request the permission
        // this provides the result[LOCATION_PERMISSION_INDEX]
        List<String> permissionsArray = new ArrayList<>();
        permissionsArray.add(Manifest.permission.ACCESS_FINE_LOCATION);

        int resultCode;

        if (runningQOrLater) {
            permissionsArray.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            resultCode = REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE;
        } else {
            resultCode = REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE;
        }

        Log.d(TAG, "Request foreground only location permission");
        ActivityCompat.requestPermissions(MainActivity.this,
                permissionsArray.toArray(new String[0]), resultCode);
    }

    /*
     * Adds a Geofence for the current clue if needed, and removes any existing Geofence. This
     * method should be called after the user has granted the location permission.  If there are
     * no more geofences, we remove the geofence and let the viewModel know that the ending hint
     * is now "active."
     */
    private void addGeofences() {
        for (int i = 0; i < GeofencingConstants.getNUM_LANDMARKS(); i++) {
            LandmarkDataObject geofenceData = GeofencingConstants.LANDMARK_DATA[i];
            Geofence geofence = new Geofence.Builder()
                    // Set the request ID, string to identify the geofence.
                    .setRequestId(geofenceData.getId())
                    // Set the circular region of this geofence.
                    .setCircularRegion(geofenceData.getLatLong().latitude,
                            geofenceData.getLatLong().longitude,
                            GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
                    )
                    // Set the expiration duration of the geofence. This geofence gets
                    // automatically removed after this period of time.
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();
            geofences.add(geofence);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                    .addOnSuccessListener(l -> {
                    })
                    .addOnFailureListener(l -> {
                        // Failed to add geofences.
                        if ((l.getMessage() != null)) {
                            Log.w(TAG, l.getMessage());
                        }
                    });
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        intent.setAction(ACTION_GEOFENCE_EVENT);
        // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        // Build the geofence request
        GeofencingRequest.Builder geofencingRequest = new GeofencingRequest.Builder()
                // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
                // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
                // is already inside that geofence.
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                // Add the geofences to be monitored by geofencing service.
                .addGeofences(geofences);
        return geofencingRequest.build();
    }
}