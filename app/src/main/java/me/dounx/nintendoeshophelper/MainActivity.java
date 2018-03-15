package me.dounx.nintendoeshophelper;

import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import GameGrabber.DownloadListener;
import GameGrabber.EUGameGrabTask;
import GameGrabber.Game;
import GameGrabber.GameLab;
import GameGrabber.JPGameGrabTask;
import GameGrabber.PriceQueryTask;
import GameGrabber.SupportedCountryGrabTask;
import GameGrabber.USGameGrabTask;

public class MainActivity extends AppCompatActivity {
    private static boolean STRICT_MODE = true;

    private DrawerLayout mDrawerLayout;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        List<Game> games = GameLab.get(this).getGames();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        GameAdapter adapter = new GameAdapter(games);
        recyclerView.setAdapter(adapter);


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

        /*
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

        Game game = new Game();
        game.setUsNsUid("70010000000141");
        game.setEuNsUid("70010000000024");
        game.setJpNsUid("70010000000027");

        //PriceQueryTask priceQueryTask = new PriceQueryTask(getApplicationContext(), mListener, game);
        //priceQueryTask.execute();
        */

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}
