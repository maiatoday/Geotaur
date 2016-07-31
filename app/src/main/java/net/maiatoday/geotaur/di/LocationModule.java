package net.maiatoday.geotaur.di;

import net.maiatoday.geotaur.location.FenceAccess;
import net.maiatoday.geotaur.location.FenceHelper;
import net.maiatoday.geotaur.location.LocationAccess;
import net.maiatoday.geotaur.location.LocationHelper;

import dagger.Module;
import dagger.Provides;

/**
 * Location Module for dependency injection
 * Created by maia on 2016/07/25.
 */
@Module
public class LocationModule {
    @Provides
    FenceAccess provideFenceAccess() {
        return new FenceHelper();
    }

    @Provides
    LocationAccess provideLocationAccess() {
        return new LocationHelper();
    }
}
