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

import android.content.Context;
import android.util.Log;

import net.maiatoday.geotaur.data.GeofenceStore;

/**
 * A wrapper class around geofence access.
 * Created by maia on 2016/07/30.
 */

public class FenceHelper implements FenceAccess {
    private static final String TAG = "FenceHelper";
    public FenceHelper(GeofenceStore store, Context context) {
    }

    @Override
    public void addGeofence(Context context, String ids) {
        GeofenceUpdateIntentService.addGeofences(context, ids);
    }

    @Override
    public void addAllGeofences(Context context) {
        GeofenceUpdateIntentService.addAllGeofences(context);
    }

    @Override
    public void removeGeofence(Context context, String ids) {
        GeofenceUpdateIntentService.removeGeofence(context, ids);
    }

    @Override
    public void queryGeofence(Context context, String id) {
        GeofenceUpdateIntentService.infoGeofences(context);
    }

    @Override
    public void testNotification(Context context, String message) {
        ActivityDetectTriggerIntentService.testNotification(context, message);
    }
}
