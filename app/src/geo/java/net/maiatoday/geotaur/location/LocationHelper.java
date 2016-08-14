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

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;

/**
 * A wrapper class around location access
 * Created by maia on 2016/07/31.
 */
public class LocationHelper implements LocationAccess,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "LocationHelper";
    private OnNewLocation listener;
    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private WeakReference<Context> context;

    @Override
    public void initialise(Context context) {
        Log.d(TAG, "initialise: ");
        buildGoogleApiClient(context);
    }

    @Override
    public void startUpdates(Context context, OnNewLocation listener) {
        Log.d(TAG, "startUpdates: ");
        this.listener = listener;
        if (apiClient != null && apiClient.isConnected()) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        apiClient, locationRequest, this);
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                Log.e(TAG, "Invalid location permission. " +
                        "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
            }
        }
    }

    @Override
    public void snapShot(Context context, OnNewLocation listener) {
        this.listener = listener;
        if (listener != null) listener.onLocationChanged(lastLocation);
    }

    @Override
    public void stopUpdates(Context context) {
        Log.d(TAG, "stopUpdates: ");
        this.listener = null;
        if (apiClient != null && apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    apiClient, this);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    private synchronized void buildGoogleApiClient(Context context) {
        this.context = new WeakReference<>(context);
        apiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(LocationConstants.LOCATION_CHECK_INTERVAL);
        locationRequest.setFastestInterval(LocationConstants.LOCATION_FASTEST_CHECK_INTERVAL);

        if (apiClient == null) {
            Log.d(TAG, "buildGoogleApiClient: urk can't setup the google api client");
        } else {
            apiClient.connect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (listener != null) listener.onLocationChanged(lastLocation);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (context.get() != null) {
            startUpdates(context.get(), listener);
            try {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        apiClient);
                if (lastLocation != null && listener != null) {
                    listener.onLocationChanged(lastLocation);
                } else {
                    Log.i(TAG, "No location");
                }
            } catch (SecurityException securityException) {
                // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                Log.e(TAG, "Invalid location permission. " +
                        "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
            }
        } else {
            listener = null;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: api connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: api connection failed");
    }
}
