package net.maiatoday.geotaur.data;

import java.util.List;

/**
 * GeofenceStore backed by firebase. Read only until I add auth.
 * Created by maia on 2016/08/04.
 */

public class GeofenceFirebaseStore implements GeofenceStore {
    @Override
    public SimpleGeofence read(String id) {
        return null;
    }

    @Override
    public void update(String id, SimpleGeofence geofence) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public List<SimpleGeofence> readAll() {
        return null;
    }

    @Override
    public List<String> getIdsAsList() {
        return null;
    }

    @Override
    public String getIdsAsString() {
        return null;
    }
}
