package com.example.tellenceparking.geofence;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

public class Constants {
    //
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 250;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<>();

    static {
//        // home
//        LANDMARKS.put("Home", new LatLng(44.412770, 26.152320));
//
//        // Mega.
//        LANDMARKS.put("Mega", new LatLng(44.414871839859074, 26.153414363674298));
//
//        // Posta
//        LANDMARKS.put("Posta", new LatLng(44.41374233763174, 26.15620175640333));

        // Rosetti Tower
        LANDMARKS.put("Rosetti Tower", new LatLng(44.441807, 26.106423));
    }
}
