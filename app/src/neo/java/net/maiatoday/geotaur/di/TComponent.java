package net.maiatoday.geotaur.di;

import net.maiatoday.geotaur.location.ActivityTriggerReceiver;
import net.maiatoday.geotaur.location.GeofenceTriggerReceiver;

/**
 * DI Injection interface
 * Created by maia on 2016/07/30.
 */

public interface TComponent extends BaseComponent {

    void inject(GeofenceTriggerReceiver target);
    void inject(ActivityTriggerReceiver target);
}
