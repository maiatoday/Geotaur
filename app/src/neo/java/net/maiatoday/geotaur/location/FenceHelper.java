package net.maiatoday.geotaur.location;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.utils.NotificationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by maia on 2016/07/30.
 */

public class FenceHelper implements FenceAccess {
    private static final String TAG = "FenceHelper";
    public static final String ENTER_PREFIX = "enter_";
    public static final String EXIT_PREFIX = "exit_";
    public static final String WALK_PREFIX = "walk_";
    public static final String DWELL_PREFIX = "dwell_";
    private static final long DWELL_MILLIS = 3000;
    private GoogleApiClient apiClient;
    private final SimpleGeofenceStore geofenceStore;

    public FenceHelper(SimpleGeofenceStore store, Context context) {
        geofenceStore = store;
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        apiClient.connect();
    }

    @Override
    public void addGeofence(Context context, String ids) {
        Log.d(TAG, "addGeofence() called with: ids = [" + ids + "]");
        // get the ids, then get the geofences from the prefs and add them all
        List<String> addIds = new ArrayList<String>(Arrays.asList(ids.split("\\s*,\\s*")));
        List<SimpleGeofence> simpleGeofenceList = geofenceStore.getGeofencesAsList();
        for (SimpleGeofence s : simpleGeofenceList) {
            if (addIds.contains(s.getId())) {
                addOneGeofence(context,
                        s.getId(),
                        s.getLatitude(),
                        s.getLongitude(),
                        s.getRadius());
            }
        }
    }

    @Override
    public void addAllGeofences(Context context) {
        Log.d(TAG, "addAllGeofences() called");
        // get the ids, then get the geofences from the prefs and add them all
        List<SimpleGeofence> simpleGeofenceList = geofenceStore.getGeofencesAsList();
        for (SimpleGeofence s : simpleGeofenceList) {
            addOneGeofence(context,
                    s.getId(),
                    s.getLatitude(),
                    s.getLongitude(),
                    s.getRadius());
        }

    }

    @Override
    public void removeGeofence(Context context, String ids) {
        Log.d(TAG, "removeGeofence() called with: ids = [" + ids + "]");
        List<String> addIds = new ArrayList<String>(Arrays.asList(ids.split("\\s*,\\s*")));
        List<SimpleGeofence> simpleGeofenceList = geofenceStore.getGeofencesAsList();
        for (SimpleGeofence s : simpleGeofenceList) {
            if (addIds.contains(s.getId())) {
                removeOneGeofence(s.getId());
            }
        }
    }

    @Override
    public void testNotification(Context context, String message) {
        NotificationUtils.notify(context, "TODO", message, R.color.colorTest);
    }

    private void addOneGeofence(Context context, final String key, double lat, double lon, double radius) {
        try {
            AwarenessFence geoFenceEnter = LocationFence.entering(lat, lon, radius);
            AwarenessFence geoFenceExit = LocationFence.exiting(lat, lon, radius);
            AwarenessFence geoFenceDwell = LocationFence.in(lat, lon, radius, DWELL_MILLIS);
            AwarenessFence walkingFence = AwarenessFence.and(LocationFence.in(lat, lon, radius, DWELL_MILLIS),
                    DetectedActivityFence.during(DetectedActivityFence.ON_FOOT));

            final String keyEnter = ENTER_PREFIX+key;
            final String keyExit  = EXIT_PREFIX+key;
            final String keyWalk  = WALK_PREFIX+key;
            final String keyDwell = DWELL_PREFIX+key;
            PendingIntent pendingIntentGeo  =  GeofenceTriggerReceiver.getTriggerPendingIntent(context);
            PendingIntent pendingIntentWalk  =  ActivityTriggerReceiver.getTriggerPendingIntent(context);
            Awareness.FenceApi.updateFences(
                    apiClient,
                    new FenceUpdateRequest.Builder()
                            .addFence(keyEnter, geoFenceEnter, pendingIntentGeo)
                            .addFence(keyExit, geoFenceExit, pendingIntentGeo)
                            .addFence(keyExit, geoFenceDwell, pendingIntentGeo)
                            .addFence(keyWalk, walkingFence, pendingIntentWalk)
                            .build())
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.i(TAG, "Fence " + key + " was successfully registered.");
                            } else {
                                Log.e(TAG, "Fence " + key + " could not be registered: " + status);
                            }
                        }
                    });

        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }
    }

    private void removeOneGeofence(final String key) {
        try {
            final String keyEnter = ENTER_PREFIX+key;
            final String keyExit  = EXIT_PREFIX+key;
            final String keyWalk  = WALK_PREFIX+key;
            Awareness.FenceApi.updateFences(
                    apiClient,
                    new FenceUpdateRequest.Builder()
                            .removeFence(keyEnter)
                            .removeFence(keyExit)
                            .removeFence(keyWalk)
                            .build()).setResultCallback(new ResultCallbacks<Status>() {
                @Override
                public void onSuccess(@NonNull Status status) {
                    Log.i(TAG, "Fence " + key + " successfully removed.");
                }

                @Override
                public void onFailure(@NonNull Status status) {
                    Log.i(TAG, "Fence " + key + " could NOT be removed.");
                }
            });

        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }

    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }


}
