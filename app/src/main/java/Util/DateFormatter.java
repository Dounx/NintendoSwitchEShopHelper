package Util;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dounx on 2018/3/23.
 */

public class DateFormatter {
    public static Date ParseStringToDate(String dateString) {
        SimpleDateFormat dateFormatUS = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        DateFormat dateFormatEU = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
        SimpleDateFormat dateFormatJP = new SimpleDateFormat("yyyy.M.d");

        //  Strict mode
        dateFormatUS.setLenient(false);
        dateFormatEU.setLenient(false);
        dateFormatJP.setLenient(false);

        Date date = null;
        try {
            date = dateFormatUS.parse(dateString);

        } catch (Exception e0) {
            try {
                date = dateFormatJP.parse(dateString);

            } catch (Exception e1) {
                try {
                    date = dateFormatEU.parse(dateString.replace("Z", " UTC"));

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
