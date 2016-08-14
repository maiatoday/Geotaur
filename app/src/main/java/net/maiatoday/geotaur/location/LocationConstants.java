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

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;

import net.maiatoday.geotaur.BuildConfig;

import java.util.HashMap;

/**
 * LocationConstants used in this sample.
 */
public final class LocationConstants {

    public static final long LOCATION_CHECK_INTERVAL = 300000;
    public static final long LOCATION_FASTEST_CHECK_INTERVAL = 300000;

    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;

    public static final String BROADCAST_ACTIVITIES = PACKAGE_NAME + ".BROADCAST_ACTIVITIES";
    public static final String BROADCAST_FENCE_INFO = PACKAGE_NAME + ".BROADCAST_FENCE_INFO";
    public static final String INFO_MESSAGE = PACKAGE_NAME + ".INFO_MESSAGE";

    public static final String ACTIVITY_ALL = PACKAGE_NAME + ".ACTIVITY_ALL";
    public static final String ACTIVITY_MOST_PROBABLE = PACKAGE_NAME + ".ACTIVITY_MOST_PROBABLE";


    private LocationConstants() {
    }
    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 168;

    /**
     * For this sample, geofences expire after 7 days.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_SML_RADIUS_IN_METERS = 150;
    public static final float GEOFENCE_MED_RADIUS_IN_METERS =  500;
    public static final float GEOFENCE_LRG_RADIUS_IN_METERS =  1000;
    public static final int NOTIFICATION_RESPONSIVENESS_MS = 4 * 1000;


    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    protected static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

}
