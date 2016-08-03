package net.maiatoday.geotaur.data;

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center and radius.
 */
public class SimpleGeofence {
    // Instance variables
    private final String id;
    private final double latitude;
    private final double longitude;
    private final float radius;
    private long expirationDuration;
    private int transitionType;
    private String title;

    /**
     * @param id The Geofence's request ID
     * @param latitude Latitude of the Geofence's center.
     * @param longitude Longitude of the Geofence's center.
     * @param radius Radius of the geofence circle.
     * @param expiration Geofence expiration duration
     * @param transition Type of Geofence transition.
     */
    public SimpleGeofence(
            String id,
            String title,
            double latitude,
            double longitude,
            float radius,
            long expiration,
            int transition) {
        // Set the instance fields from the constructor
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.expirationDuration = expiration;
        this.transitionType = transition;
    }
    // Instance field getters
    public String getId() {
        return id;
    }
    public double getLatitude() {
        return latitude;
    }
    public String getLatitudeText() {
        return String.format("%1$,.6f", latitude);
    }
    public double getLongitude() {
        return longitude;
    }
    public String getLongitudeText() {
        return String.format("%1$,.6f", longitude);
    }
    public float getRadius() {
        return radius;
    }
    public long getExpirationDuration() {
        return expirationDuration;
    }
    public int getTransitionType() {
        return transitionType;
    }
    public String getRadiusText() {return radius +"m";}
    public String getTitle() { return title; }

    /**
     * Creates a Location Services Geofence object from a
     * SimpleGeofence.
     *
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                .setRequestId(getId())
                .setTransitionTypes(transitionType)
//                .setNotificationResponsiveness(LocationConstants.NOTIFICATION_RESPONSIVENESS_MS)
                .setCircularRegion(
                        getLatitude(), getLongitude(), getRadius())
                .setExpirationDuration(expirationDuration)
                .build();
    }
}
