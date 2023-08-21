package it.units.sim.yourtube;

import androidx.appcompat.app.AppCompatDelegate;

public class SettingsManager {

    public static final String PREFERENCE_THEME_DEFAULT = "system";
    public static final String PREFERENCE_THEME = "theme_preference";

    public static void setTheme(String newTheme) {
        switch (newTheme) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

}
