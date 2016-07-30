package net.maiatoday.geotaur.helpers;

import android.content.SharedPreferences;

/**
 * Created by maia on 2016/07/25.
 */

public class PreferenceHelper {
    public static final String SHARED_PREF_NAME = "Taur";
    public static final String KEY_FIRST_TIME = "first_time";

    public static void write(SharedPreferences prefs, String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
