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

package net.maiatoday.geotaur.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.analytics.Analytics;
import net.maiatoday.geotaur.analytics.AnalyticsImpl;
import net.maiatoday.geotaur.config.RemoteConfig;
import net.maiatoday.geotaur.config.RemoteConfigImpl;
import net.maiatoday.geotaur.data.GeofencePrefsStore;
import net.maiatoday.geotaur.data.GeofenceStore;
import net.maiatoday.geotaur.helpers.FirebaseHelper;
import net.maiatoday.geotaur.helpers.PreferenceHelper;
import net.maiatoday.quip.Quip;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Application Module for dependency injection
 * Created by maia on 2016/07/25.
 */
@Module
public class ApplicationModule {
    private static final String TAG = "ApplicationModule";

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context providesContext() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(PreferenceHelper.SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    Analytics provideAnalytics(Context context) {
        return new AnalyticsImpl(context);
    }

    @Provides
    @Singleton
    RemoteConfig providesRemoteConfig(Context context) {
        RemoteConfig remoteConfig = new RemoteConfigImpl();
        remoteConfig.fetch(null);
        return remoteConfig;
    }

    @Provides
    @Named("enterQuip")
    @Singleton
    Quip provideEnterQuip(Context context, RemoteConfig remoteConfig) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_enter);
        final Quip result = new Quip(quips, remoteConfig.getBoolean("enter_random"));
        boolean useFirebase = remoteConfig.getBoolean("enter_use_firebase");
        if (useFirebase) {
            FirebaseHelper.getQuipsFromFirebase(result, FirebaseHelper.QUIP_ENTER_KEY);
        }
        return result;
    }

    @Provides
    @Named("exitQuip")
    @Singleton
    Quip provideExitQuip(Context context, RemoteConfig remoteConfig) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_exit);
        final Quip result = new Quip(quips, remoteConfig.getBoolean("exit_random"));
        boolean useFirebase = remoteConfig.getBoolean("exit_use_firebase");
        if (useFirebase) {
            FirebaseHelper.getQuipsFromFirebase(result, FirebaseHelper.QUIP_EXIT_KEY);
        }
        return result;
    }

    @Provides
    @Named("walkQuip")
    @Singleton
    Quip provideWalkQuip(Context context, RemoteConfig remoteConfig) {
        Resources res = context.getResources();
        final String[] quips = res.getStringArray(R.array.string_array_walk);
        final Quip result = new Quip(quips, remoteConfig.getBoolean("walk_random"));
        boolean useFirebase = remoteConfig.getBoolean("walk_use_firebase");
        if (useFirebase) {
            FirebaseHelper.getQuipsFromFirebase(result, FirebaseHelper.QUIP_WALK_KEY);
        }
        return result;
    }

    @Provides
    GeofenceStore providesGeofenceStore(Context context) {
        SharedPreferences preferences = provideSharedPreferences(context);
        return new GeofencePrefsStore(preferences);
    }
}