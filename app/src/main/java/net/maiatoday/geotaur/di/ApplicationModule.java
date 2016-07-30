package net.maiatoday.geotaur.di;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.firebase.analytics.FirebaseAnalytics;


import net.maiatoday.geotaur.R;
import net.maiatoday.geotaur.helpers.PreferenceHelper;
import net.maiatoday.geotaur.location.LocationAccess;
import net.maiatoday.geotaur.location.LocationWrapper;
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
    FirebaseAnalytics provideAnalytics(Context context) {
        return FirebaseAnalytics.getInstance(context);
    }

    @Provides @Named("enterQuip")
    @Singleton
    Quip provideEnterQuip(Context context) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_enter);
        return new Quip(quips);
    }

    @Provides  @Named("exitQuip")
    @Singleton
    Quip provideExitQuip(Context context) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_exit);
        return new Quip(quips);
    }

    @Provides  @Named("walkQuip")
    @Singleton
    Quip provideWalkQuip(Context context) {
        Resources res = context.getResources();
        String[] quips = res.getStringArray(R.array.string_array_walk);
        return new Quip(quips);
    }

    @Provides
    LocationAccess provideLocationAccess() {
        return new LocationWrapper();
    }
}