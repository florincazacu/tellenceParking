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
        if (intent.getAction().equals("HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT")) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

            if (geofencingEvent.hasError()) {
                String message = GeofenceUtils.errorMessage(context, geofencingEvent.getErrorCode());
                Log.e(TAG, message);
            }

            if (geofencingEvent.getGeofenceTransition() == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.v(TAG, context.getString(R.string.geofence_entered));
//                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                String fenceId;
                if (geofencingEvent.getTriggeringGeofences().isEmpty()) {
                    Log.e(TAG, "No Geofence Trigger Found!");
                    return;
                } else {
                    fenceId = geofencingEvent.getTriggeringGeofences().get(0).getRequestId();
                }

                // Check geofence against the constants listed in GeofenceUtil.kt to see if the
                // user has entered any of the locations we track for geofences.
//                int foundIndex = Arrays.stream(GeofencingConstants.LANDMARK_DATA).filter(landmarkDataObject -> landmarkDataObject.getId().equals(fenceId))
//                        .findFirst();

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

    //    @SuppressLint("ResourceType")
//    @Override
//    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
//        Intrinsics.checkParameterIsNotNull(context, "context");
//        Intrinsics.checkParameterIsNotNull(intent, "intent");
//        if (Intrinsics.areEqual(intent.getAction(), "HuntMainActivity.treasureHunt.action.ACTION_GEOFENCE_EVENT")) {
//            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//            String fenceId;
//            if (geofencingEvent.hasError()) {
//                Intrinsics.checkExpressionValueIsNotNull(geofencingEvent, "geofencingEvent");
//                fenceId = GeofenceUtils.errorMessage(context, geofencingEvent.getErrorCode());
//                Log.e("GeofenceReceiver", fenceId);
//                return;
//            }
//
//            Intrinsics.checkExpressionValueIsNotNull(geofencingEvent, "geofencingEvent");
//            if (geofencingEvent.getGeofenceTransition() == 1) {
//                Log.v("GeofenceReceiver", context.getString(1900039));
////                Toast.makeText((Context) GeofenceBroadcastReceiverJ.this, "", Toast.LENGTH_SHORT).show();
//                List var10000 = geofencingEvent.getTriggeringGeofences();
//                Intrinsics.checkExpressionValueIsNotNull(var10000, "geofencingEvent.triggeringGeofences");
//                Collection var5 = (Collection) var10000;
//                boolean var6 = false;
//                if (var5.isEmpty()) {
//                    Log.e("GeofenceReceiver", "No Geofence Trigger Found! Abort mission!");
//                    return;
//                }
//
//                Object var15 = geofencingEvent.getTriggeringGeofences().get(0);
//                Intrinsics.checkExpressionValueIsNotNull(var15, "geofencingEvent.triggeringGeofences[0]");
//                fenceId = ((Geofence) var15).getRequestId();
//                LandmarkDataObject[] $this$indexOfFirst$iv = GeofencingConstants.INSTANCE.getLANDMARK_DATA();
//                boolean var7 = false;
//                int index$iv = 0;
//                int var9 = $this$indexOfFirst$iv.length;
//
//                int var16;
//                while (true) {
//                    if (index$iv >= var9) {
//                        var16 = -1;
//                        break;
//                    }
//
//                    LandmarkDataObject it = $this$indexOfFirst$iv[index$iv];
//                    boolean var11 = false;
//                    if (Intrinsics.areEqual(it.getId(), fenceId)) {
//                        var16 = index$iv;
//                        break;
//                    }
//
//                    ++index$iv;
//                }
//
//                int foundIndex = var16;
//                if (-1 == foundIndex) {
//                    Log.e("GeofenceReceiver", "Unknown Geofence: Abort Mission");
//                    return;
//                }
//
//                var15 = ContextCompat.getSystemService(context, NotificationManager.class);
//                if (var15 == null) {
//                    throw new TypeCastException("null cannot be cast to non-null type android.app.NotificationManager");
//                }
//
//                NotificationManager notificationManager = (NotificationManager) var15;
//                NotificationUtils.sendGeofenceEnteredNotification(notificationManager, context, foundIndex);
//            }
//        }
//
//    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
//        if (geofencingEvent.hasError()) {
//            String errorMessage = GeofenceStatusCodes
//                    .getStatusCodeString(geofencingEvent.getErrorCode());
//            Log.e(TAG, errorMessage);
//            return;
//        }
//
//        // Get the transition type.
//        int transitionType = geofencingEvent.getGeofenceTransition();
//
//        // Test that the reported transition was of interest.
//        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER ||
//                transitionType == Geofence.GEOFENCE_TRANSITION_EXIT) {
//
//            // Get the geofences that were triggered. A single event can trigger
//            // multiple geofences.
//            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
//
//            // Get the transition details as a String.
//            String geofenceTransitionDetails = getGeofenceTransitionDetails(
//                    context,
//                    transitionType,
//                    triggeringGeofences
//            );
//
//            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails, context);
//            Log.i(TAG, geofenceTransitionDetails);
//        } else {
//            // Log the error.
//            Log.e(TAG, String.format("invalid transition type %s",
//                    transitionType));
//        }
//    }
//
//    private String getGeofenceTransitionDetails(
//            Context context,
//            int geofenceTransition,
//            List<Geofence> triggeringGeofences) {
//
//        String geofenceTransitionString = getTransitionString(geofenceTransition);
//
//        // Get the Ids of each geofence that was triggered.
//        ArrayList triggeringGeofencesIdsList = new ArrayList();
//        for (Geofence geofence : triggeringGeofences) {
//            triggeringGeofencesIdsList.add(geofence.getRequestId());
//        }
//        String triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList);
//
//        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;
//    }
//
//    private void sendNotification(String notificationDetails, Context context) {
//        // Create an explicit content Intent that starts the main Activity.
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//
//        // Construct a task stack.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//
//        // Add the main Activity to the task stack as the parent.
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Push the content Intent onto the stack.
//        stackBuilder.addNextIntent(notificationIntent);
//
//        // Get a PendingIntent containing the entire back stack.
//        PendingIntent notificationPendingIntent =
//                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // Get a notification builder that's compatible with platform versions >= 4
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//
//        // Define the notification settings.
//        builder.setContentTitle(notificationDetails)
//                .setContentText("Click notification to return to app")
//                .setContentIntent(notificationPendingIntent);
//
//        // Dismiss notification once the user touches it.
//        builder.setAutoCancel(true);
//
//        // Get an instance of the Notification manager
//        NotificationManager mNotificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Issue the notification
//        mNotificationManager.notify(0, builder.build());
//    }
//
//    private String getTransitionString(int transitionType) {
//        switch (transitionType) {
//            case Geofence.GEOFENCE_TRANSITION_ENTER:
//                return "transition - enter";
//            case Geofence.GEOFENCE_TRANSITION_EXIT:
//                return "transition - exit";
//            default:
//                return "unknown transition";
//        }
//    }

}

