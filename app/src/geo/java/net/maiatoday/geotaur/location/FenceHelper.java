package net.maiatoday.geotaur.location;

import android.content.Context;

/**
 * A wrapper class around geofence access.
 * Created by maia on 2016/07/30.
 */

public class FenceHelper implements FenceAccess {
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
    public void testNotification(Context context, String message) {
        ActivityDetectTriggerIntentService.testNotification(context, message);
    }
}
