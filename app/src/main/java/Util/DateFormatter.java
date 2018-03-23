package Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Dounx on 2018/3/23.
 */

public class DateFormatter {
    public Date ParseStringToDate(String dateString) {
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

    public String ParseDateToString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        return dateFormat.format(date);
    }
}
