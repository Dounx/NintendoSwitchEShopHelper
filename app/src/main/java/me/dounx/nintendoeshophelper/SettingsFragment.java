package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import GameGrabber.EUGameGrabTask;
import GameGrabber.JPGameGrabTask;
import GameGrabber.RatesQueryTask;
import GameGrabber.SupportedCountryGrabTask;
import GameGrabber.USGameGrabTask;
import Util.UserPreferences;

/**
 * Created by Dounx on 2018/3/24.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PreferenceScreen mScreen = null;
    private Context mContext = null;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference);
        mScreen = getPreferenceScreen();
        mContext = getActivity().getApplicationContext();

        final Preference updateGame = findPreference("update_game_info");
        updateGame.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateGame();
                return true;
            }
        });

        Preference updateRate = findPreference("update_rate_info");
        updateRate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateRate();
                return true;
            }
        });

        Preference updateCountry = findPreference("update_support_country");
        updateCountry.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                updateCountry();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    private void updateGame() {
        final USGameGrabTask usGameGrabTask = new USGameGrabTask(mContext);
        final EUGameGrabTask euGameGrabTask = new EUGameGrabTask(mContext);
        final JPGameGrabTask jpGameGrabTask = new JPGameGrabTask(mContext);

        usGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        euGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        jpGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (usGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        euGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        jpGameGrabTask.getStatus() != AsyncTask.Status.FINISHED) {
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    void updateRate() {
        final RatesQueryTask ratesQueryTask = new RatesQueryTask(mContext);
        ratesQueryTask.execute(UserPreferences.getStoredCurrency(mContext));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (ratesQueryTask.getStatus() != AsyncTask.Status.FINISHED) {
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    void updateCountry() {
        final SupportedCountryGrabTask supportedCountryGrabTask = new SupportedCountryGrabTask(mContext);
        supportedCountryGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (supportedCountryGrabTask.getStatus() != AsyncTask.Status.FINISHED) {
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}
