package net.maiatoday.geotaur.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;

import net.maiatoday.geotaur.location.service.GeofenceRefreshService;
import net.maiatoday.geotaur.location.service.RefreshGeofenceReceiver;

import java.util.Calendar;

/**
 * Utilities to start the GcmNetworkManager tasks and timed refresh alarms.
 * Created by maia on 2016/05/12.
 */
public class TaskUtils {

    private static final String TASK_ONE_OFF_GEOFENCE = "OneOffGeofenceRefresh";
    private static final String TASK_ONE_OFF_DAILY_GEOFENCE = "DailyOneOffGeofenceRefresh";

    private static final long PERIOD_ONCE_A_DAY_IN_SECONDS = 24 * 60 * 60L;
    private static final long HOUR_IN_SECONDS = 60 * 60L;
    private static final long TEN_MINUTES_IN_SECONDS = 10 * 60L;
    private static final long FIVE_MINUTES_IN_SECONDS = 5 * 60L;
    private static final long THIRTY_SECONDS = 30L;
    private static final int REQUEST_GEOFENCE_REFRESH = 85060;

    private TaskUtils() {
    }
    private static final String TAG = "TaskUtils";

    public static void setupStartupTasks(Context context, boolean firstTime) {

        GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(context);

        if (firstTime) {
            // Make sure we refresh the geofences as soon as we open the app only on
            // first time load.
            OneoffTask geoFenceTaskOneoff = new OneoffTask.Builder()
                    .setService(GeofenceRefreshService.class)
                    .setTag(TASK_ONE_OFF_GEOFENCE)
                    .setExecutionWindow(0L, 10L)
                    .setUpdateCurrent(true)
                    .build();
            gcmNetworkManager.schedule(geoFenceTaskOneoff);

            setAlarmRefreshGeofences(context);

            Log.d(TAG, "setupStartupTasks: Alarm is set");
        }

    }

    public static void setAlarmRefreshGeofences(Context context) {
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, RefreshGeofenceReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, REQUEST_GEOFENCE_REFRESH, intent, 0);

        // Set the alarm to start at 02:00AM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 1 day
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
        Log.d(TAG, "setAlarmRefreshGeofences: alarm set");
    }

    public static void startRefreshGeofences(Context context) {
        GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(context);
        OneoffTask geoFenceTaskOneoff = new OneoffTask.Builder()
                .setService(GeofenceRefreshService.class)
                .setTag(TASK_ONE_OFF_DAILY_GEOFENCE)
                .setExecutionWindow(0L, 60L)
                .setUpdateCurrent(true)
                .build();
        gcmNetworkManager.schedule(geoFenceTaskOneoff);
    }
}
