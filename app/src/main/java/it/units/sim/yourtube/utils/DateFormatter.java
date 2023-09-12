package it.units.sim.yourtube.utils;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {

    private final static String ITALIAN = "italiano";

    public static String formatDate(long timeInMillis, Resources resources) {
        Configuration configuration = resources.getConfiguration();
        Locale currentLocale = configuration.getLocales().get(0);

        String pattern;
        if (currentLocale.getDisplayLanguage().equals(ITALIAN)){
            pattern = "dd MMM yyyy";
        } else {
            pattern = "MMM dd, yyyy";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, currentLocale);
        return sdf.format(new Date(timeInMillis));
    }

    public static String formatDateTime(long timeInMillis, Resources resources) {
        Configuration configuration = resources.getConfiguration();
        Locale currentLocale = configuration.getLocales().get(0);
        String pattern;
        if (currentLocale.getDisplayLanguage().equals(ITALIAN)){
            pattern = "dd MMM yyyy, HH:mm";
        } else {
            pattern = "MMM dd, yyyy, hh:mm a";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(pattern, currentLocale);
        return sdf.format(new Date(timeInMillis));
    }
}
