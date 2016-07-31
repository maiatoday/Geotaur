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

public class ActivityTriggerReceiver extends BroadcastReceiver {
    private static final String TAG = "ActivityTriggerReceiver";

    @Inject
    @Named("walkQuip")
    Quip walkQuip;

    public ActivityTriggerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((TaurApplication) context.getApplicationContext()).getComponent().inject(this);
         // Awareness triggered walking
        FenceState fenceState = FenceState.extract(intent);
        String key = fenceState.getFenceKey();
        if (key.startsWith(FenceHelper.WALK_PREFIX)) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence > walking started inside geofence");
                    String geofenceId = key.substring(FenceHelper.WALK_PREFIX.length());
                    NotificationUtils.notify(context, walkQuip.blurt(), geofenceId, R.color.colorWalk);
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > walking stopped inside geofence");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > walking unknown");
                    break;
            }
        }
    }

    public static PendingIntent getTriggerPendingIntent(Context context) {
        Intent intent = new Intent(BuildConfig.APPLICATION_ID+".ACTION_RECEIVE_ACTIVITY");
         return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
