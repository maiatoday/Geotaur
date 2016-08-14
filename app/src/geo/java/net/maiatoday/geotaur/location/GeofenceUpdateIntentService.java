/*
 * MIT License
 *
 * Copyright (c) [2016] [Maia Grotepass]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.maiatoday.geotaur.location;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import net.maiatoday.geotaur.BuildConfig;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.data.GeofenceStore;
import net.maiatoday.geotaur.data.SimpleGeofence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * An {@link IntentService} to handle a request to clear geofence triggers for passed ids.
 *
 */
public class GeofenceUpdateIntentService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "GeofenceUpdateIntentSrv";

    private static final String ACTION_REMOVE_GEOFENCE = BuildConfig.APPLICATION_ID+".REMOVE_GEOFENCE";
    private static final String ACTION_ADD_GEOFENCE = BuildConfig.APPLICATION_ID+".ADD_GEOFENCE";
    private static final String ACTION_ADD_ALL_GEOFENCE = BuildConfig.APPLICATION_ID+".ADD_ALL_GEOFENCE";
    private static final String EXTRA_PARAM_GEOFENCE_IDS = BuildConfig.APPLICATION_ID+".extra.PARAM_GEOFENCE_IDS";

    @Inject
    SharedPreferences prefs;

    @Inject
    // Persistent storage for geofences
            GeofenceStore mGeofenceStorage;
    /**
     * The list of geofences from a hardcoded list
     */
    protected ArrayList<Geofence> mGeofenceList;
    List<SimpleGeofence> mSimpleGeofenceList;

    protected GoogleApiClient mGoogleApiClient;
    private String mIdsToRemove;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    public GeofenceUpdateIntentService() {
        super("GeofenceUpdateIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((TaurApplication) getApplication()).getComponent().inject(this);
    }


    /**
     * Starts this service to remove geofences with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void removeGeofence(Context context, String geofenceIds) {
        Intent intent = new Intent(context, GeofenceUpdateIntentService.class);
        intent.setAction(ACTION_REMOVE_GEOFENCE);
        intent.putExtra(EXTRA_PARAM_GEOFENCE_IDS, geofenceIds);
        context.startService(intent);
    }

    /**
     * Starts this service to remove geofences with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void addGeofences(Context context, String geofenceIds) {
        Intent intent = new Intent(context, GeofenceUpdateIntentService.class);
        intent.setAction(ACTION_ADD_GEOFENCE);
        intent.putExtra(EXTRA_PARAM_GEOFENCE_IDS, geofenceIds);
        context.startService(intent);
    }

    /**
     * Starts this service to remove geofences with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public static void addAllGeofences(Context context) {
        Intent intent = new Intent(context, GeofenceUpdateIntentService.class);
        intent.setAction(ACTION_ADD_ALL_GEOFENCE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String ids = intent.getStringExtra(EXTRA_PARAM_GEOFENCE_IDS);
            if (ACTION_REMOVE_GEOFENCE.equals(action)) {
                handleRemoveGeofenceIds(ids);
            } else if (ACTION_ADD_GEOFENCE.equals(action)) {
                handleAddGeofenceIds(ids);
            } else if (ACTION_ADD_ALL_GEOFENCE.equals(action)) {
                handleAddAllGeofenceIds();
            }
        }
    }

    private void handleAddGeofenceIds(String ids) {
        // get the ids, then get the geofences from the prefs and add them all
        List<String> addIds = new ArrayList<String>(Arrays.asList(ids.split("\\s*,\\s*")));
        mIdsToRemove = null;
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();
        mSimpleGeofenceList = mGeofenceStorage.readAll();
        populateGeofenceList(addIds);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    private void handleAddAllGeofenceIds() {
        mIdsToRemove = null;
        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();
        mSimpleGeofenceList = mGeofenceStorage.readAll();
        populateGeofenceList(mGeofenceStorage.getIdsAsList());
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }


    public void populateGeofenceList(List<String> ids) {
        for (SimpleGeofence simpleGeofence : mSimpleGeofenceList) {
            if (ids.contains(simpleGeofence.getId())) {
                mGeofenceList.add(simpleGeofence.toGeofence());
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
        } else if (mGeofenceList != null && mGeofenceList.size() > 0) {
            addAllGeofences();
        }
    }

    private void addAllGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            return;
        }
        if(!mGeofenceList.isEmpty()) {
            try {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        // The GeofenceRequest object.
                        getGeofencingRequest(),
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                logSecurityException(securityException);
            }
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(BuildConfig.APPLICATION_ID+".ACTION_RECEIVE_GEOFENCE");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void removeFenceIds(String idsToRemove) {
        List<String> ids = new ArrayList<String>(Arrays.asList(idsToRemove.split("\\s*,\\s*")));
        // Remove geofences.
        LocationServices.GeofencingApi.removeGeofences(
                mGoogleApiClient,
                ids
        ).setResultCallback(this); // Result processed in onResult().
        mIdsToRemove = null;
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
    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.d(TAG, "onResult: success, ids removed");
            notifyInfo(this, "Processed successfully");
        } else {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.d(TAG, "onResult: fail, ids not removed error: "+ errorMessage);
            notifyInfo(this, "Fail: "+errorMessage);
        }
    }

    void notifyInfo(Context context, String message) {
        Intent localIntent = new Intent(LocationConstants.BROADCAST_FENCE_INFO);
        localIntent.putExtra(LocationConstants.INFO_MESSAGE, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }
}
