package net.maiatoday.geotaur.data;

import java.util.List;

/**
 * Interface for GeofenceStore so we can have either Shared Prefs or some other implementation
 * Created by maia on 2016/08/03.
 */
public interface GeofenceStore {
    SimpleGeofence read(String id);

    void update(String id, SimpleGeofence geofence);

    void delete(String id);

    List<SimpleGeofence> readAll();

    List<String> getIdsAsList();

    String getIdsAsString();
}
