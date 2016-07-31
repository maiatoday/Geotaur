package net.maiatoday.geotaur.di;

import net.maiatoday.geotaur.location.BootReceiver;
import net.maiatoday.geotaur.ui.MainActivity;

/**
 * Base dependency injection component interface
 * Created by maia on 2016/07/31.
 */

public interface BaseComponent {
    void inject(MainActivity target);
    void inject(BootReceiver target);
}
