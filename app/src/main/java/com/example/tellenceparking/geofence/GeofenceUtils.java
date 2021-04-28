package com.example.tellenceparking.geofence;

import android.content.Context;
import android.content.res.Resources;

import com.example.tellenceparking.R;
import com.google.android.gms.location.GeofenceStatusCodes;

import org.jetbrains.annotations.NotNull;

import kotlin.jvm.internal.Intrinsics;

public final class GeofenceUtils {

    @NotNull
    public static String errorMessage(@NotNull Context context, int errorCode) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Resources resources = context.getResources();
        String errorMessage;
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                errorMessage = resources.getString(R.string.geofence_not_available);
                break;
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                errorMessage = resources.getString(R.string.geofence_too_many_geofences);
                break;
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                errorMessage = resources.getString(R.string.geofence_too_many_pending_intents);
                break;
            default:
                errorMessage = resources.getString(R.string.unknown_geofence_error);
        }

        return errorMessage;
    }
}
