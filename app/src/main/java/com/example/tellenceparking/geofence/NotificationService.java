package com.example.tellenceparking.geofence;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;

import androidx.core.app.NotificationCompat.Builder;

import com.example.tellenceparking.MainActivity;
import com.example.tellenceparking.R;
import com.example.tellenceparking.model.ParkingLot;
import com.example.tellenceparking.requests.ParkingSlotsRequest;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public final class NotificationService {
    
    private static final int NOTIFICATION_ID = 33;
    private static final String CHANNEL_ID = "GeofenceChannel";

    public static void createChannel(@NotNull Context context) {
        if (VERSION.SDK_INT >= 26) {
            NotificationChannel geofenceChannel = new NotificationChannel(CHANNEL_ID,
                    context.getString(R.string.channel_name), NotificationManager.IMPORTANCE_HIGH);
            geofenceChannel.setShowBadge(false);
            geofenceChannel.enableLights(true);
            geofenceChannel.setLightColor(Color.RED);
            geofenceChannel.enableVibration(true);
            geofenceChannel.setDescription(context.getString(R.string.channel_name));
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(geofenceChannel);
        }
    }

    public static void sendGeofenceEnteredNotification(@NotNull NotificationManager notificationManager,
                                                       @NotNull Context context, LandmarkDataObject landmarkDataObject) {
        ParkingSlotsRequest parkingSlotsRequest = new ParkingSlotsRequest();

        parkingSlotsRequest.fetchParkingLot(new ParkingSlotsRequest.SlotRequestCallback() {
            @Override
            public void onSlotsReceived(ParkingLot parkingLot) {
                int freeSlots = getFreeSlots(parkingLot);
                displayNotification(context, notificationManager, landmarkDataObject, freeSlots);
            }

            @Override
            public void onSlotsFailure() {
            }
        });
    }

    private static void displayNotification(Context context, NotificationManager notificationManager, LandmarkDataObject landmarkDataObject, int freeSlots) {
        Intent contentIntent = new Intent(context, MainActivity.class);
        contentIntent.putExtra("GEOFENCE_INDEX", GeofencingConstants.INSTANCE.getLANDMARK_DATA().indexOf(landmarkDataObject));
        PendingIntent contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String location = context.getString(landmarkDataObject.getName());
        Builder builder = new Builder(context, "GeofenceChannel")
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.content_text, location, freeSlots))
                .setPriority(1)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private static int getFreeSlots(ParkingLot parkingLot) {
        AtomicInteger freeSlots = new AtomicInteger();
        if (parkingLot == null) {
            return freeSlots.get();
        }

        parkingLot.getParking_spaces().forEach((floor, parkingSpaces) ->
                parkingSpaces.forEach(parkingSpace -> {
                    if (parkingSpace.getStatus() == 1) {
                        freeSlots.getAndIncrement();
                    }
                }));
        return freeSlots.get();
    }
}


