package net.maiatoday.geotaur.di;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Application Component for dependency injection
 * Created by maia on 2016/07/25.
 */

@Singleton
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent extends TComponent {
}
