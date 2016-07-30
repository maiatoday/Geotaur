package net.maiatoday.geotaur.location.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.maiatoday.geotaur.utils.TaskUtils;

/**
 * Broadcast receiver triggered by an alarm to refresh the geofence triggers.
 * Typically triggered once a day.
 * Created by lnesenberend on 2016/06/13.
 */

public class RefreshGeofenceReceiver extends BroadcastReceiver {

    private static final String TAG = "RefreshGeofenceReceiver";

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        //context.startService cannot be used to call the GeofenceRefreshService, because that is how GCM starts the service.
        //For now we can use the receiver to schedule a onceOffTask that refreshes the geofences
        Log.d(TAG, "onReceive: Geofence Alarm Triggered");

        TaskUtils.startRefreshGeofences(context);

        Log.d(TAG, "onReceive: Geofence Refresh Scheduled");
    }
}
