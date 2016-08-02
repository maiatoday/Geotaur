package net.maiatoday.geotaur.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by maia on 2016/08/02.
 */

public class AnalyticsImpl implements Analytics {

    public AnalyticsImpl(Context context) {
        FirebaseAnalytics.getInstance(context);
    }

    @Override
    public void logEvent(Context context, String event, Bundle bundle) {
        FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
    }
}
