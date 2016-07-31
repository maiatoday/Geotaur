package net.maiatoday.geotaur.location;

import android.content.Context;
import android.util.Log;

/**
 * A wrapper class around geofence access.
 * Created by maia on 2016/07/30.
 */

public class FenceHelper implements FenceAccess {
    private static final String TAG = "FenceHelper";
    public FenceHelper(SimpleGeofenceStore store, Context context) {
    }

    @Override
    public void addGeofence(Context context, String ids) {
        GeofenceUpdateIntentService.addGeofences(context, ids);
    }

    @Override
    public void addAllGeofences(Context context) {
        GeofenceUpdateIntentService.addAllGeofences(context);
    }

    @Override
    public void removeGeofence(Context context, String ids) {
        GeofenceUpdateIntentService.removeGeofence(context, ids);
    }

    @Override
    public void queryGeofence(Context context, String id) {
        Log.d(TAG, "queryGeofence: Hrmph not possible");
    }

    @Override
    public void testNotification(Context context, String message) {
        ActivityDetectTriggerIntentService.testNotification(context, message);
    }
}
