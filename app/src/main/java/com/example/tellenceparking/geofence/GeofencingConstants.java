package com.example.tellenceparking.geofence;

import com.example.tellenceparking.R;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;


public final class GeofencingConstants {
    @NotNull
    public static final LandmarkDataObject[] LANDMARK_DATA;
    private static final int NUM_LANDMARKS;
    public static final float GEOFENCE_RADIUS_IN_METERS = 250.0F;
    @NotNull
    public static final GeofencingConstants INSTANCE = new GeofencingConstants();

    @NotNull
    public final LandmarkDataObject[] getLANDMARK_DATA() {
        return LANDMARK_DATA;
    }

    public static int getNUM_LANDMARKS() {
        return NUM_LANDMARKS;
    }

    static {
        LANDMARK_DATA = new LandmarkDataObject[]{
//                new LandmarkDataObject("Mega", R.string.ferry_building_hint, R.string.mega_location, new LatLng(44.414871839859074D, 26.153414363674298D)),
//                new LandmarkDataObject("Home", R.string.home_location, new LatLng(44.41277D, 26.15232D)),
//                new LandmarkDataObject("Posta", R.string.ferry_building_hint, R.string.posta_location, new LatLng(44.41374233763174D, 26.15620175640333D)),
                new LandmarkDataObject("Rosetti Tower", R.string.rosetti_location, new LatLng(44.441807, 26.106423))};
        NUM_LANDMARKS = LANDMARK_DATA.length;
    }
}
