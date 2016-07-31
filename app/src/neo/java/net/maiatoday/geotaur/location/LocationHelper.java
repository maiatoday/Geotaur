package net.maiatoday.geotaur.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

/**
 * Created by maia on 2016/07/31.
 */

public class LocationHelper implements LocationAccess {
    private static final String TAG = "LocationHelper";
    private GoogleApiClient apiClient;

    private Location lastLocation;
    private OnNewLocation listener;

    @Override
    public void initialise(Context context) {
        Log.d(TAG, "initialise: ");
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .build();
        apiClient.connect();
    }

    @Override
    public void startUpdates(Context context, final OnNewLocation listener) {
        this.listener = listener;
        Log.d(TAG, "startUpdates: ");
    }

    @Override
    public void snapShot(Context context, final OnNewLocation listener) {
        this.listener = listener;
        try {
            Awareness.SnapshotApi.getLocation(apiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {

                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            if (!locationResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Could not get location.");
                                if (listener != null) listener.onLocationChanged(lastLocation);
                                return;
                            }
                            Location location = locationResult.getLocation();
                            Log.i(TAG, "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                            lastLocation = location;
                            if (listener != null) listener.onLocationChanged(lastLocation);
                        }
                    });
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            Log.e(TAG, "Invalid location permission. " +
                    "You need to use ACCESS_FINE_LOCATION with geofences", securityException);

        }
    }

    @Override
    public void stopUpdates(Context context) {
        Log.d(TAG, "stopUpdates: ");
    }
}
