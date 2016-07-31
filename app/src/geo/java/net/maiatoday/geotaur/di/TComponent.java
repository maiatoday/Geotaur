package net.maiatoday.geotaur.di;

import net.maiatoday.geotaur.location.BootReceiver;
import net.maiatoday.geotaur.location.ActivityDetectTriggerIntentService;
import net.maiatoday.geotaur.location.GeofenceTriggerReceiver;
import net.maiatoday.geotaur.location.GeofenceUpdateIntentService;
import net.maiatoday.geotaur.ui.MainActivity;

/**
 * Created by maia on 2016/07/30.
 */

public interface TComponent extends BaseComponent {
    void inject(ActivityDetectTriggerIntentService target);
    void inject(GeofenceUpdateIntentService target);
    void inject(GeofenceTriggerReceiver target);

}
