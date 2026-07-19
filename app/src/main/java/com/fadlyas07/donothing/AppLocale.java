package com.fadlyas07.donothing;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

final class AppLocale {

    static final String LANGUAGE_INDONESIAN = "id";
    static final String LANGUAGE_ENGLISH = "en";

    private static final String PREFERENCES_NAME =
            "nothing_language_preferences";

    private static final String KEY_LANGUAGE =
            "selected_language";

    private AppLocale() {
    }

    static Context wrap(Context context) {
        String language = getLanguage(context);
        Locale locale = new Locale(language);

        Locale.setDefault(locale);

        Configuration configuration =
                new Configuration(
                        context
                                .getResources()
                                .getConfiguration()
                );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocales(
                    new LocaleList(locale)
            );
        } else {
            configuration.setLocale(locale);
        }

        return context.createConfigurationContext(
                configuration
        );
    }

    static String getLanguage(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREFERENCES_NAME,
                        Context.MODE_PRIVATE
                );

        String savedLanguage =
                preferences.getString(
                        KEY_LANGUAGE,
                        null
                );

        if (
                LANGUAGE_INDONESIAN.equals(savedLanguage)
                        || LANGUAGE_ENGLISH.equals(savedLanguage)
        ) {
            return savedLanguage;
        }

        return getSystemLanguage(context);
    }

    static void setLanguage(
            Context context,
            String language
    ) {
        if (
                !LANGUAGE_INDONESIAN.equals(language)
                        && !LANGUAGE_ENGLISH.equals(language)
        ) {
            throw new IllegalArgumentException(
                    "Unsupported language: " + language
            );
        }

        context
                .getSharedPreferences(
                        PREFERENCES_NAME,
                        Context.MODE_PRIVATE
                )
                .edit()
                .putString(
                        KEY_LANGUAGE,
                        language
                )
                .apply();
    }

    private static String getSystemLanguage(
            Context context
    ) {
        Configuration configuration =
                context
                        .getResources()
                        .getConfiguration();

        Locale systemLocale;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            systemLocale =
                    configuration
                            .getLocales()
                            .get(0);
        } else {
            systemLocale = configuration.locale;
        }

        if (
                systemLocale != null
                        && LANGUAGE_INDONESIAN.equals(
                                systemLocale.getLanguage()
                        )
        ) {
            return LANGUAGE_INDONESIAN;
        }

        return LANGUAGE_ENGLISH;
    }
}
