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
import net.maiatoday.geotaur.helpers.PreferenceHelper;
import net.maiatoday.geotaur.location.SimpleGeofenceStore;
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
        return new Quip(quips, remoteConfig.getBoolean("enter_random"));
    }

    @Provides
    @Named("exitQuip")
    @Singleton
    Quip provideExitQuip(Context context, RemoteConfig remoteConfig) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_exit);
        return new Quip(quips, remoteConfig.getBoolean("exit_random"));
    }

    @Provides
    @Named("walkQuip")
    @Singleton
    Quip provideWalkQuip(Context context, RemoteConfig remoteConfig) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_walk);
        return new Quip(quips, remoteConfig.getBoolean("walk_random"));
    }

    @Provides
    SimpleGeofenceStore providesGeofenceStore(SharedPreferences preferences) {
        return new SimpleGeofenceStore(preferences);
    }
}