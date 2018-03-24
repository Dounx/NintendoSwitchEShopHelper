package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import GameGrabber.CountryGrabTask;
import GameGrabber.DownloadListener;
import GameGrabber.EUGameGrabTask;
import GameGrabber.Game;
import GameGrabber.GameLab;
import GameGrabber.JPGameGrabTask;
import GameGrabber.RatesQueryTask;
import GameGrabber.SupportedCountryLab;
import GameGrabber.USGameGrabTask;
import Util.QueryPreferences;
import Util.UserPreferences;

public class MainActivity extends AppCompatActivity {
    private static boolean STRICT_MODE = false;

    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private List<Game> mGames;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GameAdapter mGameAdapter;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;

        if (STRICT_MODE) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        // Init Database or Shared Preferences
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt("version",0);
        if (currentVersion > lastVersion) {
            try {
                CopySqliteFileFromRawToDatabases("GameBase.db");

                // Init User SharedPreferences todo
                final Locale country;
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    country = this.getResources().getConfiguration().getLocales().get(0);
                } else {
                    country = this.getResources().getConfiguration().locale;
                }

                DownloadListener listener = new DownloadListener() {
                    @Override
                    public void onSuccess() {
                        UserPreferences.setStoredCurrency(mContext, SupportedCountryLab.get(mContext).getCountry(country.getCountry()).getCurrency());
                    }

                    @Override
                    public void onFailed() {

                    }
                };
                CountryGrabTask countryGrabTask = new CountryGrabTask(mContext, listener);
                countryGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                UserPreferences.setStoredLanguage(this,"English");

            } catch (IOException e) {
                e.printStackTrace();
            }
            prefs.edit().putInt("version", currentVersion).apply();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.all_games);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_game_all);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_game_all:
                        mGames = GameLab.get(mContext).getGames();
                        mGameAdapter.setData(mGames);
                        mGameAdapter.notifyDataSetChanged();
                        getSupportActionBar().setTitle(R.string.nav_all_games);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_game_new:
                        mGames = GameLab.get(mContext).getNewGames();
                        mGameAdapter.setData(mGames);
                        mGameAdapter.notifyDataSetChanged();
                        getSupportActionBar().setTitle(R.string.nav_not_released_games);
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.nav_game_discount:
                        mGames = GameLab.get(mContext).getDiscountGames();
                        mGameAdapter.setData(mGames);
                        mGameAdapter.notifyDataSetChanged();
                        getSupportActionBar().setTitle(R.string.nav_discount_games);
                        mDrawerLayout.closeDrawers();
                        break;
                    case  R.id.nav_settings:
                        // todo clear the select status
                        mDrawerLayout.closeDrawers();
                        Intent intent = new Intent(mContext, SettingsActivity.class);
                        startActivity(intent);
                        return false;
                    default:
                }
                return true;
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setDistanceToTriggerSync(200);
        mSwipeRefreshLayout.setProgressViewEndTarget(false, 200);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mGames = GameLab.get(this).getGames();
        FixedPreloadSizeProvider<Game> sizeProvider = new FixedPreloadSizeProvider(500, 250);
        ListPreloader.PreloadModelProvider modelProvider = new MyPreloadModelProvider();
        RecyclerViewPreloader<Game> preLoader = new RecyclerViewPreloader<>(Glide.with(this), modelProvider, sizeProvider, 20);

        final RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.addOnScrollListener(preLoader);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), mLayoutManager.getOrientation()));
        mGameAdapter = new GameAdapter(this, mGames);
        mRecyclerView.setAdapter(mGameAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    if (mFab.getVisibility() == View.INVISIBLE) {
                        mFab.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mLayoutManager.findFirstVisibleItemPosition()  == 0) {
                        mFab.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RatesQueryTask ratesQueryTask = new RatesQueryTask(this);
        ratesQueryTask.execute(UserPreferences.getStoredCurrency(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPreferences.setStoredQuery(mContext, query);
                updateItems();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                QueryPreferences.setStoredQuery(mContext, newText.equals("")? null : newText);
                updateItems();
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                QueryPreferences.setStoredQuery(mContext, null);
                updateItems();
                searchView.onActionViewCollapsed();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    private class MyPreloadModelProvider implements ListPreloader.PreloadModelProvider {
        @Override
        @NonNull
        public List<String> getPreloadItems(int position) {
            String url = mGames.get(position).getIconUrl();
            if (TextUtils.isEmpty(url)) {
                return Collections.emptyList();
            }
            return Collections.singletonList(url);
        }

        @Override
        @NonNull
        public RequestBuilder getPreloadRequestBuilder(Object item) {
            return GlideApp.with(mContext)
                            .load((String) item);
        }
    }

    private void refreshData() {
        final USGameGrabTask usGameGrabTask = new USGameGrabTask(mContext);
        final EUGameGrabTask euGameGrabTask = new EUGameGrabTask(mContext);
        final JPGameGrabTask jpGameGrabTask = new JPGameGrabTask(mContext);
        final RatesQueryTask ratesQueryTask = new RatesQueryTask(mContext);

        usGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        euGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        jpGameGrabTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        ratesQueryTask.execute("CNY");

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (usGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        euGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        jpGameGrabTask.getStatus() != AsyncTask.Status.FINISHED ||
                        ratesQueryTask.getStatus() != AsyncTask.Status.FINISHED) {
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String pos = getSupportActionBar().getTitle().toString();
                        if (pos.equals(mContext.getString(R.string.nav_all_games))) {
                            mGames = GameLab.get(mContext).getGames();
                        } else if (pos.equals(mContext.getString(R.string.nav_not_released_games))) {
                            mGames = GameLab.get(mContext).getNewGames();
                        } else if (pos.equals(mContext.getString(R.string.nav_discount_games))) {
                            mGames = GameLab.get(mContext).getDiscountGames();
                        }
                        mGameAdapter.setData(mGames);
                        mGameAdapter.notifyDataSetChanged();

                        mSwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, getResources().getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(mContext);
        List<Game> games;

        String pos = getSupportActionBar().getTitle().toString();
        if (pos.equals(mContext.getString(R.string.nav_all_games))) {
            mGames = GameLab.get(mContext).getGames();
        } else if (pos.equals(mContext.getString(R.string.nav_not_released_games))) {
            mGames = GameLab.get(mContext).getNewGames();
        } else if (pos.equals(mContext.getString(R.string.nav_discount_games))) {
            mGames = GameLab.get(mContext).getDiscountGames();
        }

        if (query == null) {
            games = mGames;
        } else {
            games = new ArrayList<>();
            for (Game game : mGames) {
                if (game.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    games.add(game);
                }
            }
        }
        mGames = games;
        mGameAdapter.setData(mGames);
        mGameAdapter.notifyDataSetChanged();
    }

    public String  CopySqliteFileFromRawToDatabases(String SqliteFileName) throws IOException {
        File dir = new File("data/data/" + this.getPackageName() + "/databases");

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }

        File file= new File(dir, SqliteFileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;

        if (!file.exists()) {
            try {
                file.createNewFile();

                inputStream = this.getClass().getClassLoader().getResourceAsStream("assets/" + SqliteFileName);
                outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[2048];
                int len ;

                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer,0,len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return file.getPath();
    }
}