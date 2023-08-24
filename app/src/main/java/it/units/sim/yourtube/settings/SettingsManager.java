package it.units.sim.yourtube.settings;

import android.content.res.Configuration;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsManager {

    public static final String PREFERENCE_THEME_DEFAULT = "system";
    public static final String PREFERENCE_LANGUAGE_DEFAULT = "system";
    public static final String PREFERENCE_THEME = "theme_preference";
    public static final String PREFERENCE_LANGUAGE = "language_preference";

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

    public static void setLanguage(Resources resources, String newLanguage) {
        Locale locale = newLanguage.equals(PREFERENCE_LANGUAGE_DEFAULT) ?
                Locale.getDefault() : new Locale(newLanguage);
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

}