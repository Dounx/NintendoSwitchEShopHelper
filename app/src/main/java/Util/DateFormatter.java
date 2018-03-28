package Util;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dounx on 2018/3/23.
 */

public class DateFormatter {
    public static Date ParseStringToDate(String dateString) {
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        SimpleDateFormat dateFormatEU = new SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH);
        SimpleDateFormat dateFormatJP = new SimpleDateFormat("yyyy.m.d", Locale.JAPAN);

        Date date = null;
        try {
            date = dateFormatUS.parse(dateString);
        } catch (Exception e0) {
            try {
                date = dateFormatEU.parse(dateString);
            } catch (Exception e1) {
                try {
                    date = dateFormatJP.parse(dateString);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return date;
    }

    public static String ParseDateToString(Date date, Locale locale, Context context) {
        SimpleDateFormat dateFormat;
        if (UserPreferences.getStoredLanguage(context).equals("Chinese")) {
            dateFormat = new SimpleDateFormat("yyyy年 MMM d日", locale);
        } else {
            dateFormat = new SimpleDateFormat("MMM d, yyyy", locale);
        }
        return dateFormat.format(date);
    }
}
