package net.maiatoday.geotaur.config;

/**
 * Created by maia on 2016/08/02.
 */

public interface RemoteConfig {
    void fetch(Callback callback);
    boolean getBoolean(String configKey);

    interface Callback {
        void onSuccess();
        void onFailure();
    }
}
