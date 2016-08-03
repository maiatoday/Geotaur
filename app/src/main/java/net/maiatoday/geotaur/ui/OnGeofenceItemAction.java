package net.maiatoday.geotaur.ui;


import net.maiatoday.geotaur.data.SimpleGeofence;

/**
 * Created by maia on 2016/04/05.
 */
public interface OnGeofenceItemAction {
    void onItemClick(SimpleGeofence item);
    void onItemEdit(SimpleGeofence item);
    void onItemInfo(SimpleGeofence item);
    void onItemRemoved(String id);
}
