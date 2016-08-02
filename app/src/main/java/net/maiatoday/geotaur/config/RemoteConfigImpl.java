package net.maiatoday.geotaur.config;

import android.util.Log;

import com.android.annotations.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import net.maiatoday.geotaur.BuildConfig;
import net.maiatoday.geotaur.R;

/**
 * Created by maia on 2016/08/02.
 */

public class RemoteConfigImpl implements RemoteConfig {
    private static final String TAG = "RemoteConfigImpl";
    private final FirebaseRemoteConfig remoteConfig;

    public RemoteConfigImpl() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        // Define Firebase Remote Config Settings.
        FirebaseRemoteConfigSettings firebaseRemoteConfigSettings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setDeveloperModeEnabled(true)
                        .build();

        remoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        remoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    @Override
    public void fetch(final Callback callback) {
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        int cacheExpiration = 43200;
        if (BuildConfig.DEBUG) {
            cacheExpiration = 0;
        }
        remoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Fetch Succeeded");
                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            remoteConfig.activateFetched();
                            if (callback != null) callback.onSuccess();
                        } else {
                            Log.d(TAG, "Fetch failed");
                            if (callback != null) callback.onFailure();
                        }
                    }
                });

    }

    @Override
    public boolean getBoolean(String configKey) {
        return remoteConfig.getBoolean(configKey);
    }
}
