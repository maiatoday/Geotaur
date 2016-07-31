package net.maiatoday.geotaur.location;

import android.content.Context;
import android.util.Log;

/**
 * Created by maia on 2016/07/31.
 */

public class LocationHelper implements LocationAccess {
    private static final String TAG = "LocationHelper";
    @Override
    public void initialise(Context context) {
        Log.d(TAG, "initialise: ");
    }

    @Override
    public void startUpdates(Context context, OnNewLocation listener) {
        Log.d(TAG, "startUpdates: ");
    }

    @Override
    public void stopUpdates(Context context) {
        Log.d(TAG, "stopUpdates: ");
    }
}
