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
