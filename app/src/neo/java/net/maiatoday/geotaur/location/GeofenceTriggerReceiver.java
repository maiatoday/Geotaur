package net.maiatoday.geotaur.location;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;

import net.maiatoday.geotaur.BuildConfig;
import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.utils.NotificationUtils;
import net.maiatoday.quip.Quip;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Broadcast receiver to catch an exit and entry to a geofence
 * Created by maia on 2016/07/14.
 */

public class GeofenceTriggerReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceTriggerReceiver";

    @Inject
    @Named("enterQuip")
    Quip enterQuip;
    @Inject
    @Named("exitQuip")
    Quip exitQuip;

    public GeofenceTriggerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((TaurApplication) context.getApplicationContext()).getComponent().inject(this);
        Log.d(TAG, "onReceive: Got a geofence trigger");
        FenceState fenceState = FenceState.extract(intent);
        String key = fenceState.getFenceKey();
        int state = fenceState.getCurrentState();
        if (key.startsWith(FenceHelper.ENTER_PREFIX)) {
            switch (state) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence >  enter geofence");
                    NotificationUtils.notify(context,
                            enterQuip.blurt(),
                            key,
                            R.color.colorEnter);
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > no longer enter geofence");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > enter geofence unknown");
                    break;
            }
        } else if (key.startsWith(FenceHelper.EXIT_PREFIX)) {
            switch (state) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence >  exit geofence");
                    NotificationUtils.notify(context,
                            exitQuip.blurt(),
                            key,
                            R.color.colorExit);
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > no longer exit geofence");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > exit geofence unknown");
                    break;
            }

        } else if (key.startsWith(FenceHelper.DWELL_PREFIX)) {
            switch (state) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence >  dwell geofence");
                    NotificationUtils.notify(context,
                            enterQuip.blurt(),
                            key,
                            R.color.colorDwell);
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > no longer dwell geofence");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > dwell geofence unknown");
                    break;
            }

        }


    }

    public static PendingIntent getTriggerPendingIntent(Context context) {
        Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".ACTION_RECEIVE_GEOFENCE");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
