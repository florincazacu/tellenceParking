package com.example.tellenceparking.geofence;

import com.example.tellenceparking.R;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class GeofencingConstants {
    @NotNull
    private static final List<LandmarkDataObject> LANDMARK_DATA;
    public static final float GEOFENCE_RADIUS_IN_METERS = 300.0F;
    @NotNull
    public static final GeofencingConstants INSTANCE = new GeofencingConstants();

    @NotNull
    public final List<LandmarkDataObject> getLANDMARK_DATA() {
        return Collections.unmodifiableList(LANDMARK_DATA);
    }

    static {
        LANDMARK_DATA = new ArrayList<>(Arrays.asList(
//                new LandmarkDataObject("Mega", R.string.mega_location, new LatLng(44.414871839859074D, 26.153414363674298D)),
                new LandmarkDataObject("Home", R.string.home_location, new LatLng(44.41277D, 26.15232D)),
//                new LandmarkDataObject("Posta", R.string.posta_location, new LatLng(44.41374233763174D, 26.15620175640333D)),
                new LandmarkDataObject("Rosetti Tower", R.string.rosetti_location, new LatLng(44.441807, 26.106423))
        ));
    }
}
