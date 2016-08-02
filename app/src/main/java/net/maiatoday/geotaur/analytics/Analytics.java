package net.maiatoday.geotaur.analytics;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by maia on 2016/08/02.
 */

public interface Analytics {
    void logEvent(Context context, String event, Bundle bundle);
}
