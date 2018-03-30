package me.dounx.nintendoeshophelper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.DisplayMetrics;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Locale;

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
    private ProgressBar mProgressBar = null;
    private ProgressDialog mProgressDialog = null;

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preference);

        mScreen = getPreferenceScreen();
        mContext = getActivity();
        mProgressBar =  getActivity().findViewById(R.id.settings_progress_bar);

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

        if (s.equals("language")) {
            Resources resources =getResources();
            Configuration config = resources.getConfiguration();
            DisplayMetrics dm = resources.getDisplayMetrics();

            if (UserPreferences.getStoredLanguage(mContext).equals("Chinese")) {
                config.locale = Locale.CHINA;
            } else if (UserPreferences.getStoredLanguage(mContext).equals("English")) {
                config.locale = Locale.US;
            }
            resources.updateConfiguration(config, dm);

            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setTitle(getString(R.string.restart));
            dialog.setMessage(getString(R.string.restart_info));
            dialog.setCancelable(true);
            dialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }

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
                if (!getActivity().isDestroyed()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

        showProgressDialog(getString(R.string.update_game), euGameGrabTask);
    }

    private void updateRate() {
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
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

        showProgressDialog(getString(R.string.update_rate), ratesQueryTask);
    }

    private void updateCountry() {
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
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();

        showProgressDialog(getString(R.string.update_country), supportedCountryGrabTask);
    }

    private void showProgressDialog(String title, final AsyncTask task) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(title);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if (task.getStatus() == AsyncTask.Status.RUNNING) {
                    task.cancel(true);
                }
            }
        });
        mProgressDialog = progressDialog;
        progressDialog.show();
    }
}
