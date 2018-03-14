package me.dounx.nintendoeshophelper;

import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import GameGrabber.DownloadListener;
import GameGrabber.EUGameGrabTask;
import GameGrabber.Game;
import GameGrabber.JPGameGrabTask;
import GameGrabber.PriceQueryTask;
import GameGrabber.SupportedCountryGrabTask;
import GameGrabber.USGameGrabTask;

public class MainActivity extends AppCompatActivity {
    private static boolean STRICT_MODE = true;

    private DownloadListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (STRICT_MODE) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toast successToast = Toast.makeText(this, "Grab success!", Toast.LENGTH_SHORT);
        final Toast failToast = Toast.makeText(this, "Grab fail!", Toast.LENGTH_SHORT);

        mListener = new DownloadListener() {
            @Override
            public void onSuccess() {
                Log.d("TAG", "Success!");
                successToast.show();
            }

            @Override
            public void onFailed() {
                failToast.show();
            }
        };

        USGameGrabTask usGameGrabTask = new USGameGrabTask(this, mListener);
        EUGameGrabTask euGameGrabTask = new EUGameGrabTask(this, mListener);
        JPGameGrabTask jpGameGrabTask = new JPGameGrabTask(this, mListener);
        SupportedCountryGrabTask supportCountryGrabTask = new SupportedCountryGrabTask(this, mListener);

        usGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        euGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        jpGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        supportCountryGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        String countryCode;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            countryCode = this.getResources().getConfiguration().getLocales().toString();
        } else {
            countryCode = this.getResources().getConfiguration().locale.getCountry();
        }
        Log.d("SupportedCountry", countryCode);

        //// After getting all the data
        /*
        Game game = new Game();
        game.setUsNsUid("70010000000141");
        game.setEuNsUid("70010000000024");
        game.setJpNsUid("70010000000027");

        PriceQueryTask priceQueryTask = new PriceQueryTask(getApplicationContext(), mListener, game);
        priceQueryTask.execute();
        */
    }
}
