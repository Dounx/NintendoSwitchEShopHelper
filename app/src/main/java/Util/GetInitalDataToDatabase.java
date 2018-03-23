package Util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import GameGrabber.EUGameGrabTask;
import GameGrabber.JPGameGrabTask;
import GameGrabber.SupportedCountryGrabTask;
import GameGrabber.USGameGrabTask;

/**
 * Created by Dounx on 2018/3/22.
 */

public class GetInitalDataToDatabase {
    public void getInitalDataToDatabase(Context context) {
        final USGameGrabTask usGameGrabTask = new USGameGrabTask(context);
        final EUGameGrabTask euGameGrabTask = new EUGameGrabTask(context);
        final JPGameGrabTask jpGameGrabTask = new JPGameGrabTask(context);
        final SupportedCountryGrabTask supportedCountryGrabTask = new SupportedCountryGrabTask(context);

        usGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        euGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        jpGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        supportedCountryGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (usGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        euGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        jpGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        supportedCountryGrabTask.getStatus() != AsyncTask.Status.FINISHED) {
                }
                Log.d("Util", "Get Inital Data To Database Success");
            }
        }).start();
    }

}
