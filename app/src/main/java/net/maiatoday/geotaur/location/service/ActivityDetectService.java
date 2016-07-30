package net.maiatoday.geotaur.location.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.location.LocationConstants;

/**
 * This service is used to setup activity detecting
 */
public class ActivityDetectService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private static final String TAG = "ActivityDetectService";
    public static final String EXTRA_GEOFENCE_ID = "geofence_id";
    private String mGeofenceIds = "Unknown";

    public ActivityDetectService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

        protected GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public void requestUpdates() {
        Log.d(TAG, "requestUpdates: "+mGeofenceIds);
        if (!mGoogleApiClient.isConnected()) {
            Log.d(TAG, "requestUpdates: " + getString(R.string.not_connected));
            return;
        }
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                LocationConstants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    public void removeUpdates() {
        Log.d(TAG, "removeUpdates: ");
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);
    }

    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        intent.putExtra(EXTRA_GEOFENCE_ID, mGeofenceIds);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Log.i(TAG, "Successfully added or removed activity detection.");
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        super.onStartCommand(intent, flags, startId);
        mGeofenceIds = intent.getStringExtra(EXTRA_GEOFENCE_ID);
        mGoogleApiClient.connect();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeUpdates();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        requestUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public static void stopDetecting(Context context) {
        Log.d(TAG, "stopDetecting: ");

        Intent intent = new Intent(context, ActivityDetectService.class);
        intent.addCategory(TAG);
        context.stopService(intent);
    }

    public static void startDetecting(Context context, String geofenceIds) {
        Log.d(TAG, "startDetecting: ");
        Intent intent = new Intent(context, ActivityDetectService.class);
        intent.putExtra(ActivityDetectService.EXTRA_GEOFENCE_ID, geofenceIds);
        intent.addCategory(TAG);
        context.startService(intent);
    }
}
