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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.utils.NotificationUtils;
import net.maiatoday.quip.Quip;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 */
public class ActivityDetectTriggerIntentService extends IntentService {
    protected static final String TAG = "detection_is";
    private static final String ACTION_TEST = "testNotification";
    private static final String EXTRA_IDS = "ids";
    private String mGeoFenceId = "Unknown";

    @Inject
    @Named("walkQuip")
    Quip walkQuip;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public ActivityDetectTriggerIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((TaurApplication) getApplication()).getComponent().inject(this);
    }

    /**
     * Sends an intent to test the notification
     *
     * @see IntentService
     */
    public static void testNotification(Context context, String ids) {
        Intent intent = new Intent(context, ActivityDetectTriggerIntentService.class);
        intent.setAction(ACTION_TEST);
        intent.putExtra(EXTRA_IDS, ids);
        context.startService(intent);
    }

    /**
     * Handles incoming intents.
     *
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (action != null && ACTION_TEST.equals(action)) {
                String ids = intent.getStringExtra(EXTRA_IDS);
                NotificationUtils.notify(this, walkQuip.blurt(), ids, R.color.colorTest);
            } else {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                Intent localIntent = new Intent(LocationConstants.BROADCAST_ACTIVITIES);
                mGeoFenceId = intent.getStringExtra(ActivityDetectUpdateService.EXTRA_GEOFENCE_ID);

                // Get the list of the probable activities associated with the current state of the
                // device. Each activity is associated with a confidence level, which is an int between
                // 0 and 100.
                ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();
                DetectedActivity mostProbable = result.getMostProbableActivity();

                // Log each activity.
                Log.i(TAG, "activities detected");
                // Broadcast the list of detected activities.
                localIntent.putExtra(LocationConstants.ACTIVITY_ALL, detectedActivities);
                localIntent.putExtra(LocationConstants.ACTIVITY_MOST_PROBABLE, mostProbable);
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

                String strStatus = "";
                for (DetectedActivity thisActivity : detectedActivities) {
                    strStatus += getActivityString(thisActivity.getType()) + thisActivity.getConfidence() + "%\n";
                    Log.d(TAG, "onHandleIntent: " + strStatus);
                }
                Log.d(TAG, "onReceive: mostProbableActivity" + getActivityString(mostProbable.getType()));

                if (mostProbable.getType() == DetectedActivity.ON_FOOT) {
//                  We transitioned to ON_FOOT give a notification.
//                  GeofenceUpdateIntentService.removeGeofence(this, mGeoFenceId);
                    NotificationUtils.notify(this, walkQuip.blurt(), mGeoFenceId, R.color.colorWalk);
                   ActivityDetectUpdateService.stopDetecting(this);
                }
            }
        }
    }
    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public String getActivityString(int detectedActivityType) {
        Resources resources = this.getResources();
        switch (detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }
}