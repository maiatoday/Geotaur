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

package net.maiatoday.geotaur.data;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Storage for geofence values, implemented in SharedPreferences.
 */
public class GeofencePrefsStore implements GeofenceStore {
    /// Key to store a list of all ids
    public static final String KEY_ALL_IDS = "KEY_ALL_IDS";
    /*
     * Keys for flattened geofences stored in SharedPreferences
     */
    public static final String KEY_LATITUDE = "KEY_LATITUDE";

    public static final String KEY_LONGITUDE = "KEY_LONGITUDE";

    public static final String KEY_RADIUS = "KEY_RADIUS";

    public static final String KEY_EXPIRATION_DURATION = "KEY_EXPIRATION_DURATION";

    public static final String KEY_TRANSITION_TYPE = "KEY_TRANSITION_TYPE";

    // The prefix for flattened geofence keys
    public static final String KEY_PREFIX = ".KEY";
    /*
     * Invalid values, used to test geofence storage when
     * retrieving geofences
     */
    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;
    // The SharedPreferences object in which geofences are stored
    private final SharedPreferences mPrefs;

    // Create the SharedPreferences storage with private access only
    public GeofencePrefsStore(SharedPreferences preferences) {
        mPrefs = preferences;
    }
    /**
     * Returns a stored geofence by its id, or returns null
     * if it's not found.
     *
     * @param id The ID of a stored geofence
     * @return A geofence defined by its center and radius. See
     */
    @Override
    public SimpleGeofence read(String id) {
            /*
             * Get the latitude for the geofence identified by id, or
             * INVALID_FLOAT_VALUE if it doesn't exist
             */
        double lat = mPrefs.getFloat(
                getGeofenceFieldKey(id, KEY_LATITUDE),
                INVALID_FLOAT_VALUE);
            /*
             * Get the longitude for the geofence identified by id, or
             * INVALID_FLOAT_VALUE if it doesn't exist
             */
        double lng = mPrefs.getFloat(
                getGeofenceFieldKey(id, KEY_LONGITUDE),
                INVALID_FLOAT_VALUE);
            /*
             * Get the radius for the geofence identified by id, or
             * INVALID_FLOAT_VALUE if it doesn't exist
             */
        float radius = mPrefs.getFloat(
                getGeofenceFieldKey(id, KEY_RADIUS),
                INVALID_FLOAT_VALUE);
            /*
             * Get the expiration duration for the geofence identified
             * by id, or INVALID_LONG_VALUE if it doesn't exist
             */
        long expirationDuration = mPrefs.getLong(
                getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
                INVALID_LONG_VALUE);
            /*
             * Get the transition type for the geofence identified by
             * id, or INVALID_INT_VALUE if it doesn't exist
             */
        int transitionType = mPrefs.getInt(
                getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
                INVALID_INT_VALUE);
        // If none of the values is incorrect, return the object
        if (
                lat != INVALID_FLOAT_VALUE &&
                        lng != INVALID_FLOAT_VALUE &&
                        radius != INVALID_FLOAT_VALUE &&
                        expirationDuration !=
                                INVALID_LONG_VALUE &&
                        transitionType != INVALID_INT_VALUE) {

            // Return a true Geofence object
            return new SimpleGeofence(id,
                    id, lat, lng, radius, expirationDuration,
                    transitionType);
            // Otherwise, return null.
        } else {
            return null;

        }
    }
    /**
     * Save a geofence.
     * @param geofence The SimpleGeofence containing the
     * values you want to save in SharedPreferences
     */
    @Override
    public void update(String id, SimpleGeofence geofence) {
            /*
             * Get a SharedPreferences editor instance. Among other
             * things, SharedPreferences ensures that updates are atomic
             * and non-concurrent
             */

        Set<String> allIds = addIdToAllIds(id);
        SharedPreferences.Editor editor = mPrefs.edit();
        // Write the Geofence values to SharedPreferences
        editor.putFloat(
                getGeofenceFieldKey(id, KEY_LATITUDE),
                (float) geofence.getLatitude());
        editor.putFloat(
                getGeofenceFieldKey(id, KEY_LONGITUDE),
                (float) geofence.getLongitude());
        editor.putFloat(
                getGeofenceFieldKey(id, KEY_RADIUS),
                geofence.getRadius());
        editor.putLong(
                getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
                geofence.getExpirationDuration());
        editor.putInt(
                getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
                geofence.getTransitionType());
        editor.putStringSet(KEY_ALL_IDS, allIds);
        // Commit the changes
        editor.apply();
    }

    @Override
    public void delete(String id) {
            /*
             * Remove a flattened geofence object from storage by
             * removing all of its keys
             */
        Set<String> allIds = removeIdFromAllIds(id);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_LONGITUDE));
        editor.remove(getGeofenceFieldKey(id, KEY_RADIUS));
        editor.remove(getGeofenceFieldKey(id,
                KEY_EXPIRATION_DURATION));
        editor.remove(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE));
        editor.putStringSet(KEY_ALL_IDS, allIds);
        editor.apply();
    }


    /**
     * Get all the geofences stores as a list.
     * @return List of all the stored geofences
     */
    @Override
    public List<SimpleGeofence> readAll() {
        List<SimpleGeofence> allGeofences = new ArrayList<>();
        Set<String> allIdsSet = mPrefs.getStringSet(KEY_ALL_IDS, null);
        if (allIdsSet != null) {
            for (String id: allIdsSet) {
                allGeofences.add(read(id));
            }
        }
        return allGeofences;
    }


    /**
     * Get a list of the stored geofence ids.
     * @return List of id strings
     */
    @Override
    public List<String> getIdsAsList() {
        Set<String> allIdsSet = mPrefs.getStringSet(KEY_ALL_IDS, null);
        List<String> ids;
        if (allIdsSet != null) {
            ids = new ArrayList<>(allIdsSet.size());
            ids.addAll(allIdsSet);
        } else {
            ids = new ArrayList<>(0);
        }
        return ids;
    }

    @Override
    public String getIdsAsString() {
        StringBuilder result = new StringBuilder();
        Set<String> allIdsSet = mPrefs.getStringSet(KEY_ALL_IDS, null);
        int i = 0;
        for (String id: allIdsSet) {
            result.append(id);
            i++;
            if (i < allIdsSet.size()) {
                result.append(",");
            }


        }
        return result.toString();
    }


    /**
     * Given a Geofence object's ID and the name of a field
     * (for example, KEY_LATITUDE), return the key name of the
     * object's values in SharedPreferences.
     *
     * @param id The ID of a Geofence object
     * @param fieldName The field represented by the key
     * @return The full key name of a value in SharedPreferences
     */
    private String getGeofenceFieldKey(String id,
                                       String fieldName) {
        return KEY_PREFIX + "_" + id + "_" + fieldName;
    }


    /**
     * Make a set with all the ids and the passed id. This does not write the set to the preferences.
     * @param id String id to add to the set.
     * @return Set of all the ids including the passed id.
     */
    private Set<String> addIdToAllIds(String id) {
        Set<String> allIdsSet = mPrefs.getStringSet(KEY_ALL_IDS, new HashSet<String>());
        allIdsSet.add(id);
        return allIdsSet;
    }


    /**
     * Make a set of all the ids but remove the passed id. This does not write the set to the preferences.
     * @param id String id to remove from the set.
     * @return Set of all the ids with the passed id removed.
     */
    private Set<String> removeIdFromAllIds(String id) {
        Set<String> allIdsSet = mPrefs.getStringSet(KEY_ALL_IDS, new HashSet<String>());
        allIdsSet.remove(id);
        return allIdsSet;
    }

}