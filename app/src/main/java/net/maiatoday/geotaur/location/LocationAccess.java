package net.maiatoday.geotaur.location;

import android.content.Context;

import net.maiatoday.geotaur.ui.MainActivity;

/**
 * Interface to wrap geofence access
 * Created by maia on 2016/07/30.
 */

public interface LocationAccess {
    void addGeofence(Context context, String ids);
    void addAllGeofences(Context context);
    void removeGeofence(Context context, String ids);

    void testNotification(Context context, String message);
}
