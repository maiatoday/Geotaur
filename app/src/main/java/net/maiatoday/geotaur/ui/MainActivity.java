package net.maiatoday.geotaur.ui;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.maiatoday.geotaur.BuildConfig;
import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.databinding.ActivityMainBinding;
import net.maiatoday.geotaur.helpers.PreferenceHelper;
import net.maiatoday.geotaur.location.GeofenceErrorMessages;
import net.maiatoday.geotaur.location.LocationConstants;
import net.maiatoday.geotaur.location.SimpleGeofence;
import net.maiatoday.geotaur.location.SimpleGeofenceStore;
import net.maiatoday.geotaur.location.service.ActivityDetectService;
import net.maiatoday.quip.Quip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements AddGeoDialogFragment.OnAddGeofenceListener,
                GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>,OnGeofenceItemAction {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION_FINE = 9000;
    @Inject
    SharedPreferences prefs;

    @Inject
    FirebaseAnalytics analytics;

    @Inject
    @Named("enterQuip")
    Quip enterQuip;

    @Inject
    @Named("exitQuip")
    Quip exitQuip;

    @Inject
    @Named("walkQuip")
    Quip walkQuip;

    private boolean firstTime;
    private ActivityMainBinding binding;
    private Button mAddGeofencesButton;
    private Button mRemoveGeofencesButton;
    private View mMainView;

    // Persistent storage for geofences
    private SimpleGeofenceStore mGeofenceStorage;
    List<SimpleGeofence> mSimpleGeofenceList;
    private RecyclerView mLandmarkRV;
    private GeofenceListAdapter mAdapter;
    private Location mLastLocation;
    private String mLastAction;
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TaurApplication) getApplication()).getComponent().inject(this);
        firstTime = prefs.getBoolean(PreferenceHelper.KEY_FIRST_TIME, true);
        PreferenceHelper.write(prefs, PreferenceHelper.KEY_FIRST_TIME, false);
        analytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        // Get the UI widgets.
        mMainView = binding.geoMain;
        mAddGeofencesButton = binding.addGeofencesButton;
        mRemoveGeofencesButton = binding.removeGeofencesButton;

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<>();
        // Instantiate a new geofence storage area
        mGeofenceStorage = new SimpleGeofenceStore(this);

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        if (mayUseLocation()) {
            setButtonsEnabledState();
        } else {
            setButtonsAllDisabled();
        }

        mSimpleGeofenceList = new ArrayList<>();
        mGeofenceList.clear();
        mSimpleGeofenceList = mGeofenceStorage.getGeofencesAsList();
        populateGeofenceList();
        mLandmarkRV = (RecyclerView) findViewById(R.id.geofence_list);
        mAdapter = new GeofenceListAdapter(mSimpleGeofenceList, this);
        mLandmarkRV.setAdapter(mAdapter);
        mLandmarkRV.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new GeofenceTouchHelper(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mLandmarkRV);

        // Kick off the request to build GoogleApiClient.

        buildGoogleApiClient();

        FloatingActionButton fab = binding.fab;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddGeoDialog();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/**
      * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
      */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        if (mGoogleApiClient == null) {
            Toast.makeText(this, R.string.error_api_build, Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    private void showAddGeoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        double myLat = mLastLocation != null ? mLastLocation.getLatitude() : 0.0;
        double myLong = mLastLocation != null ? mLastLocation.getLongitude() : 0.0;
        AddGeoDialogFragment dialogFragment = AddGeoDialogFragment.newInstance(LocationConstants.GEOFENCE_MED_RADIUS_IN_METERS, myLat, myLong);
        dialogFragment.show(fm, AddGeoDialogFragment.getFragmentTag());
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "Connected to GoogleApiClient");
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.i(TAG, "Last Location Lat:" + mLastLocation.getLatitude() + " Long:" + mLastLocation.getLongitude());
            }
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
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

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        addAllGeofences();
    }

    private void addAllGeofences() {
        if (mGeofenceList.size() > 0) {
            mLastAction += "Refresh all Geofences.\n";
            if (!mGoogleApiClient.isConnected()) {
                Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
                return;
            }

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
        } else {
            mLastAction += "Geofence list is empty.\n";
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        removeAllGeofences();
    }

    private void removeAllGeofences() {

        mLastAction = "Remove all Geofences.\n";
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ActivityDetectService.stopDetecting(this);
            List<String> ids = mGeofenceStorage.getGeofenceIds();
            if (ids == null || ids.size() == 0) {
                Toast.makeText(this, getString(R.string.nothing_to_remove), Toast.LENGTH_SHORT).show();
                return;
            }
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    ids
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void removeOneGeofence(String id) {
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(this, getString(R.string.nothing_to_remove), Toast.LENGTH_SHORT).show();
            return;
        }
        mLastAction = "Geo Remove " + id + "\n";
        List<String> ids = new ArrayList<>(1);
        ids.add(id);
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            ActivityDetectService.stopDetecting(this);
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    ids
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p/>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    mLastAction + getString(R.string.geofences_updated),
                    Toast.LENGTH_SHORT
            ).show();
            mLastAction = "";
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
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
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        Intent intent = new Intent(BuildConfig.APPLICATION_ID+".ACTION_RECEIVE_GEOFENCE");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        for (SimpleGeofence simpleGeofence : mSimpleGeofenceList) {
            mGeofenceList.add(simpleGeofence.toGeofence());
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        mAddGeofencesButton.setEnabled(true);
        mRemoveGeofencesButton.setEnabled(true);
    }

    private void setButtonsAllDisabled() {
        mAddGeofencesButton.setEnabled(false);
        mRemoveGeofencesButton.setEnabled(false);
    }

    private boolean mayUseLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
            Snackbar.make(mMainView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_FINE);
                        }
                    });
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION_FINE);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_FINE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setButtonsEnabledState();
            }
        }
    }

    @Override
    public void onAddGeofence(String title, String radius, String latitude, String longtitude) {
        Log.d(TAG, "onAddGeofence: " + title + " " + radius + " " + latitude + ", " + longtitude);
        SimpleGeofence simpleGeofence = new SimpleGeofence(title,
                Double.valueOf(latitude),
                Double.valueOf(longtitude),
                Float.valueOf(radius),
                LocationConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT);
        mSimpleGeofenceList.add(simpleGeofence);
        mAdapter.notifyItemInserted(mSimpleGeofenceList.size() - 1);
        populateGeofenceList();
        mGeofenceStorage.setGeofence(title, simpleGeofence);
        mLastAction = "Add " + title + "\n";
        addAllGeofences();
    }

    @Override
    public void onItemClick(SimpleGeofence item) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", item.getLatitude(), item.getLongitude());
        Log.d(TAG, "onClick: " + uri);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public void onItemRemoved(String id) {
        mGeofenceStorage.clearGeofence(id);
        removeOneGeofence(id);
    }
}
