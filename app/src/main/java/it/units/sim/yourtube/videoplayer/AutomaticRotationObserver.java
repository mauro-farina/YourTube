package it.units.sim.yourtube.videoplayer;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

public class AutomaticRotationObserver extends ContentObserver {

    public interface Callback {
        void onAutoRotationChanged();
    }

    private boolean autoRotationEnabled;
    private final ContentResolver contentResolver;
    private final Callback callback;

    AutomaticRotationObserver(Handler handler,
                              ContentResolver contentResolver,
                              Callback callback) {
        super(handler);
        this.contentResolver = contentResolver;
        int autoRotationSettingState = getAutoRotationSettingState();
        this.autoRotationEnabled = autoRotationSettingState == 1;
        this.callback = callback;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        if (uri != null
                && uri.equals(Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION))) {
            int newAutoRotationSettingState = getAutoRotationSettingState();
            autoRotationEnabled = newAutoRotationSettingState == 1;
            callback.onAutoRotationChanged();
        }
    }

    private int getAutoRotationSettingState() {
        return Settings.System.getInt(
                contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                0
        );
    }

    public boolean isAutoRotationEnabled() {
        return autoRotationEnabled;
    }
}
