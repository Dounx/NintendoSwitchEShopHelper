package Util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Dounx on 2018/3/24.
 */

public class UserPreferences {
    private static final String PREF_LANGUAGE = "language";
    private static final String PREF_CURRENCY = "currency";

    public static String getStoredLanguage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_LANGUAGE, null);
    }

    public static void setStoredLanguage(Context context, String language) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_LANGUAGE, language)
                .apply();
    }

    public static String getStoredCurrency(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_CURRENCY, null);
    }

    public static void setStoredCurrency(Context context, String currency) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CURRENCY, currency)
                .apply();
    }
}
