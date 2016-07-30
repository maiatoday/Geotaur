package net.maiatoday.geotaur.location;

import android.content.Context;
import android.util.Log;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.utils.NotificationUtils;

/**
 * Created by maia on 2016/07/30.
 */

public class LocationWrapper implements LocationAccess {
    private static final String TAG = "LocationWrapper";
    @Override
    public void addGeofence(Context context, String ids) {
        Log.d(TAG, "addGeofence() called with: ids = [" + ids + "]");
    }

    @Override
    public void addAllGeofences(Context context) {
        Log.d(TAG, "addAllGeofences() called");

    }

    @Override
    public void removeGeofence(Context context, String ids) {
        Log.d(TAG, "removeGeofence() called with: ids = [" + ids + "]");

    }

    @Override
    public void testNotification(Context context, String message) {
        NotificationUtils.notify(context, "TODO", message, R.color.colorPrimary);
    }
}
