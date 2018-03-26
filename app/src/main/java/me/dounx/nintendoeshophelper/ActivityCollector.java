package me.dounx.nintendoeshophelper;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dounx on 2018/3/26.
 */

public class ActivityCollector {
    public static List<Activity> sActivities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        sActivities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        sActivities.remove(activity);
    }

    public static void finshAll() {
        for (Activity activity : sActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        sActivities.clear();
    }
}
