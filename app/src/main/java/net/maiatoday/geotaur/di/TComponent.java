package net.maiatoday.geotaur.di;

import net.maiatoday.geotaur.location.service.DetectedActivitiesIntentService;
import net.maiatoday.geotaur.location.service.GeofenceReceiver;
import net.maiatoday.geotaur.ui.MainActivity;

/**
 * Created by maia on 2016/07/30.
 */

public interface TComponent {
    void inject(MainActivity target);
    void inject(DetectedActivitiesIntentService target);
    void inject(GeofenceReceiver target);

}
