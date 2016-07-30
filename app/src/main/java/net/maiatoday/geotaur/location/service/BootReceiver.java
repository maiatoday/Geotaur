package net.maiatoday.geotaur.location.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.maiatoday.geotaur.utils.TaskUtils;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: Boot resetting geofences and setting up alarm");
        TaskUtils.startRefreshGeofences(context);
        TaskUtils.setAlarmRefreshGeofences(context);
    }

}
