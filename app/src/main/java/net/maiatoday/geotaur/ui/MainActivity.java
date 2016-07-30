package net.maiatoday.geotaur.ui;

import android.annotation.TargetApi;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.analytics.FirebaseAnalytics;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.databinding.ActivityMainBinding;
import net.maiatoday.geotaur.helpers.PreferenceHelper;
import net.maiatoday.geotaur.location.LocationAccess;
import net.maiatoday.geotaur.location.LocationConstants;
import net.maiatoday.geotaur.location.SimpleGeofence;
import net.maiatoday.geotaur.location.SimpleGeofenceStore;
import net.maiatoday.quip.Quip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements AddGeoDialogFragment.OnAddGeofenceListener,
                GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnGeofenceItemAction {
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

    @Inject
    LocationAccess locationAccess;

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
     * Used when requesting to add or remove geofences.
     */
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

        // Instantiate a new geofence storage area
        mGeofenceStorage = new SimpleGeofenceStore(this);

        if (mayUseLocation()) {
            setButtonsEnabledState();
        } else {
            setButtonsAllDisabled();
        }

        mSimpleGeofenceList = new ArrayList<>();
        mSimpleGeofenceList = mGeofenceStorage.getGeofencesAsList();
        mLandmarkRV = (RecyclerView) findViewById(R.id.geofence_list);
        mAdapter = new GeofenceListAdapter(mSimpleGeofenceList, this);
        mLandmarkRV.setAdapter(mAdapter);
        mLandmarkRV.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new GeofenceTouchHelper(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mLandmarkRV);

        // Kick off the request to build GoogleApiClient.  Used to get location only.

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

    public void testNotificationButtonHandler(View view) {
        locationAccess.testNotification(this, "HelloWorld");
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesButtonHandler(View view) {
        locationAccess.addAllGeofences(this);
    }


    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        locationAccess.removeGeofence(this, mGeofenceStorage.getGeofenceIdsAsString());
    }


    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

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
        mGeofenceStorage.setGeofence(title, simpleGeofence);
        mLastAction = "Add " + title + "\n";
        locationAccess.addGeofence(this, title);
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
        locationAccess.removeGeofence(this, id);
    }


}
