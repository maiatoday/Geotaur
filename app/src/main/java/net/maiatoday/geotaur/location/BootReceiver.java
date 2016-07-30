package net.maiatoday.geotaur.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import net.maiatoday.geotaur.TaurApplication;

import javax.inject.Inject;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    @Inject
    LocationAccess locationProvider;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((TaurApplication) context.getApplicationContext()).getComponent().inject(this);
        Log.d(TAG, "onReceive: Boot resetting geofences");
        locationProvider.addAllGeofences(context);
    }

}
