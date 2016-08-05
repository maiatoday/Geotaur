package net.maiatoday.geotaur.location.service;

import android.content.Context;

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

    }
}
