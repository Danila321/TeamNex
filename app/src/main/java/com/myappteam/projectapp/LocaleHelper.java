package com.myappteam.projectapp;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {
    public static void saveLanguage(Context context, String lang) {
        context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().putString("language", lang).apply();
    }

    public static String getLanguage(Context context) {
        return context.getSharedPreferences("Settings", Context.MODE_PRIVATE).getString("language", Locale.getDefault().getLanguage());
    }

    public static Context applyLanguage(Context context) {
        String lang = getLanguage(context);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
