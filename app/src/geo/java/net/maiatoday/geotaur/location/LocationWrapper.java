package net.maiatoday.geotaur.location;

import android.content.Context;

import net.maiatoday.geotaur.location.ActivityDetectTriggerIntentService;
import net.maiatoday.geotaur.location.GeofenceUpdateIntentService;
import net.maiatoday.geotaur.location.LocationAccess;

/**
 * Created by maia on 2016/07/30.
 */

public class LocationWrapper implements LocationAccess {
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
