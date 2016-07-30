package net.maiatoday.geotaur.location.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import net.maiatoday.geotaur.location.GeofenceErrorMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An {@link IntentService} to handle a request to clear geofence triggers for passed ids.
 *
 */
public class GeofenceUpdateIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "GeofenceUpdateIntentSrv";

    private static final String ACTION_REMOVE_GEOFENCE = "com.mysidekick.mysidekick.service.action.REMOVE_GEOFENCE";
    private static final String EXTRA_PARAM_GEOFENCE_IDS = "com.mysidekick.mysidekick.service.extra.PARAM_GEOFENCE_IDS";

    protected GoogleApiClient mGoogleApiClient;
    private String mIdsToRemove;

    public GeofenceUpdateIntentService() {
        super("GeofenceUpdateIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void removeGeofence(Context context, String geofenceIds) {
        Intent intent = new Intent(context, GeofenceUpdateIntentService.class);
        intent.setAction(ACTION_REMOVE_GEOFENCE);
        intent.putExtra(EXTRA_PARAM_GEOFENCE_IDS, geofenceIds);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_REMOVE_GEOFENCE.equals(action)) {
                final String ids = intent.getStringExtra(EXTRA_PARAM_GEOFENCE_IDS);
                handleRemoveGeofenceIds(ids);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleRemoveGeofenceIds(String ids) {
        mIdsToRemove = ids;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to GoogleApiClient");
        if (!TextUtils.isEmpty(mIdsToRemove)) {
            removeFenceIds(mIdsToRemove);
        }
    }

    private void removeFenceIds(String idsToRemove) {
        List<String> ids = new ArrayList<String>(Arrays.asList(idsToRemove.split("\\s*,\\s*")));
        // Remove geofences.
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                ids
        ).setResultCallback(this); // Result processed in onResult().
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "onResult: success, ids removed");
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.d(TAG, "onResult: fail, ids not removed error: "+ errorMessage);
        }
    }
}
