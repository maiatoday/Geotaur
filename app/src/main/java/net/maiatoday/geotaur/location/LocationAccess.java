package net.maiatoday.geotaur.location;

import android.content.Context;
import android.location.Location;

/**
 * Created by maia on 2016/07/31.
 */

public interface LocationAccess {
    void initialise(Context context);
    void startUpdates(Context context, OnNewLocation listener);
    void snapShot(Context context, OnNewLocation listener);
    void stopUpdates(Context context);

    interface OnNewLocation {
        void onLocationChanged(Location location);
    }

}
