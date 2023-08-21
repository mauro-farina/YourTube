package it.units.sim.yourtube;

import android.content.Context;
import android.content.res.Configuration;

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

    public static void setLanguage(Context context, String newLanguage) {
        Locale locale = newLanguage.equals(PREFERENCE_LANGUAGE_DEFAULT) ?
                Locale.getDefault() : new Locale(newLanguage);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

}