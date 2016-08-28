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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.awareness.fence.FenceState;

import net.maiatoday.geotaur.BuildConfig;
import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.TaurApplication;
import net.maiatoday.geotaur.utils.NotificationUtils;
import net.maiatoday.quip.Quip;

import javax.inject.Inject;
import javax.inject.Named;

public class ActivityTriggerReceiver extends BroadcastReceiver {
    private static final String TAG = "ActivityTriggerReceiver";

    @Inject
    @Named("walkQuip")
    Quip walkQuip;

    public ActivityTriggerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ((TaurApplication) context.getApplicationContext()).getComponent().inject(this);
         // Awareness triggered walking
        FenceState fenceState = FenceState.extract(intent);
        String key = fenceState.getFenceKey();
        if (key.startsWith(FenceHelper.WALKING_IN_DWELL_PREFIX)) {
            switch (fenceState.getCurrentState()) {
                case FenceState.TRUE:
                    Log.i(TAG, "Fence > walking started inside geofence");
                    String geofenceId = key.substring(FenceHelper.WALKING_IN_DWELL_PREFIX.length());
                    NotificationUtils.notify(context, walkQuip.blurt(), geofenceId, R.color.colorWalk);
                    break;
                case FenceState.FALSE:
                    Log.i(TAG, "Fence > walking stopped inside geofence");
                    break;
                case FenceState.UNKNOWN:
                    Log.i(TAG, "Fence > walking unknown");
                    break;
            }
        }
    }

    public static PendingIntent getTriggerPendingIntent(Context context) {
        Intent intent = new Intent(BuildConfig.APPLICATION_ID+".ACTION_RECEIVE_ACTIVITY");
         return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
