package com.example.tellenceparking.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GeofencingClientManager {

    private static final String TAG = "GeofencingClientManager";
    private static final String ACTION_GEOFENCE_EVENT = "MainActivity.ACTION_GEOFENCE_EVENT";

    private final Activity activity;
    private final List<Geofence> geofences = new ArrayList<>();
    private final GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    public GeofencingClientManager(Activity activity) {
        this.activity = activity;
        geofencingClient = LocationServices.getGeofencingClient(this.activity);
    }

    /*
     * Adds a Geofence for the current clue if needed, and removes any existing Geofence. This
     * method should be called after the user has granted the location permission.  If there are
     * no more geofences, we remove the geofence and let the viewModel know that the ending hint
     * is now "active."
     */
    public void addGeofences() {
        if (geofences.isEmpty()) {
            for (LandmarkDataObject geofenceData : GeofencingConstants.INSTANCE.getLANDMARK_DATA()) {
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
            Log.d(TAG, "geofences added");

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
        intent.setAction(ACTION_GEOFENCE_EVENT);
        // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        if (isAppInForeground()) {
            geofencePendingIntent = null;
        } else {
            geofencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
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

    private boolean isAppInForeground() {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName.equalsIgnoreCase(activity.getPackageName())
                && services.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            isActivityFound = true;
        }

        return isActivityFound;
    }

}
