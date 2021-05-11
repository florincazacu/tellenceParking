package com.example.tellenceparking.geofence;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.tellenceparking.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.stream.IntStream;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("MainActivity.ACTION_GEOFENCE_EVENT")) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            if (geofencingEvent.hasError()) {
                String message = GeofenceUtil.errorMessage(context, geofencingEvent.getErrorCode());
                Log.e(TAG, message);
            }

            if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.v(TAG, context.getString(R.string.geofence_entered));
                String fenceId;
                if (geofencingEvent.getTriggeringGeofences().isEmpty()) {
                    Log.e(TAG, "No Geofence Trigger Found!");
                    return;
                } else {
                    fenceId = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
                }

                // Check geofence against the constants listed in GeofenceUtil to see if the
                // user has entered any of the locations we track for geofences.
                int foundIndex = IntStream.range(0, GeofencingConstants.LANDMARK_DATA.length)
                        .filter(index -> GeofencingConstants.LANDMARK_DATA[index].getId().equals(fenceId))
                        .findFirst().orElse(-1);

                // Unknown Geofences aren't helpful to us
                if (foundIndex == -1) {
                    Log.e(TAG, "Unknown Geofence:");
                    return;
                }

                NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);
                NotificationUtils.sendGeofenceEnteredNotification(notificationManager, context, foundIndex);
            }
        }
    }
}

