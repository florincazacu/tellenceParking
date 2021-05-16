package com.example.tellenceparking.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.tellenceparking.BuildConfig;
import com.example.tellenceparking.R;
import com.example.tellenceparking.geofence.GeofencingClientManager;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class LocationPermissionManager {

    private static final String TAG = "LocationPermissionManager";
    private static final int LOCATION_PERMISSION_INDEX = 0;
    private static final int BACKGROUND_LOCATION_PERMISSION_INDEX = 1;
    private static final int REQUEST_TURN_DEVICE_LOCATION_ON = 29;
    private static final int REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33;
    private static final int REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34;
    private final boolean runningQOrLater = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private final Activity activity;
    private final GeofencingClientManager geofencingClientManager;

    public LocationPermissionManager(Activity activity) {
        this.activity = activity;
        geofencingClientManager = new GeofencingClientManager(this.activity);
    }

    /*
     *  When we get the result from asking the user to turn on device location, we call
     *  checkDeviceLocationSettingsAndStartGeofence again to make sure it's actually on, but
     *  we don't resolve the check to keep the user from seeing an endless loop.
     */
    public void onActivityResult(int requestCode) {
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            // We don't rely on the result code, but just check the location setting again
            checkPermissionsAndStartGeofencing();
        }
    }

    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
        if (grantResults.length == 0 || grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
                (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                        grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                                PackageManager.PERMISSION_DENIED)) {
            // Permission denied.
            Log.e(TAG, "permission denied");
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.settings, view ->
                            activity.startActivity(new Intent()
                                    .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null))
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)))
                    .show();
        } else {
            checkPermissionsAndStartGeofencing();
        }
    }

    public void checkPermissionsAndStartGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettingsAndStartGeofence();
        } else {
            requestForegroundAndBackgroundLocationPermissions();
        }
    }

    /*
     *  Uses the Location Client to check the current state of location settings, and gives the user
     *  the opportunity to turn on location services within our app.
     */
    public void checkDeviceLocationSettingsAndStartGeofence() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> locationSettingsResponseTask = settingsClient.checkLocationSettings(locationSettingsRequest);

        locationSettingsResponseTask.addOnFailureListener((exception) -> {
            if (exception instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                    ResolvableApiException e = (ResolvableApiException) exception;
                    e.startResolutionForResult(activity, REQUEST_TURN_DEVICE_LOCATION_ON);
                } catch (IntentSender.SendIntentException sendEx) {
                    Log.e(TAG, "Error getting location settings resolution: " + sendEx.getMessage());
                }
            } else {
                Snackbar.make(activity.findViewById(android.R.id.content), R.string.location_required_error, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, v -> checkDeviceLocationSettingsAndStartGeofence()).show();
            }
        });

        locationSettingsResponseTask.addOnCompleteListener((result) -> {
            if (result.isSuccessful()) {
                geofencingClientManager.addGeofences();
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
                PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION));
        boolean backgroundPermissionApproved;
        if (runningQOrLater) {
            backgroundPermissionApproved = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
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
        ActivityCompat.requestPermissions(activity,
                permissionsArray.toArray(new String[0]), resultCode);
    }
}
