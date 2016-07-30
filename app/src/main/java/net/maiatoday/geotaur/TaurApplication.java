package net.maiatoday.geotaur;

import android.app.Application;

import net.maiatoday.geotaur.di.ApplicationModule;
import net.maiatoday.geotaur.di.DaggerApplicationComponent;
import net.maiatoday.geotaur.di.TComponent;

/**
 * Geo-Neo-Taur application
 * Created by maia on 2016/07/30.
 */

public class TaurApplication extends Application {
    private TComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = createComponent();
    }

    public TComponent createComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public TComponent getComponent() {
        return component;
    }
}
