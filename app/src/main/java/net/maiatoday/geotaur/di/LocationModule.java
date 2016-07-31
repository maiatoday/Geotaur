package net.maiatoday.geotaur.di;

import android.content.Context;

import net.maiatoday.geotaur.location.FenceAccess;
import net.maiatoday.geotaur.location.FenceHelper;
import net.maiatoday.geotaur.location.LocationAccess;
import net.maiatoday.geotaur.location.LocationHelper;
import net.maiatoday.geotaur.location.SimpleGeofenceStore;

import dagger.Module;
import dagger.Provides;

/**
 * Location Module for dependency injection
 * Created by maia on 2016/07/25.
 */
@Module
public class LocationModule {
    @Provides
    FenceAccess provideFenceAccess(SimpleGeofenceStore store, Context context) {
        return new FenceHelper(store, context);
    }

    @Provides
    LocationAccess provideLocationAccess() {
        return new LocationHelper();
    }
}
