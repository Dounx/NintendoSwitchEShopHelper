package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import GameGrabber.DownloadListener;
import GameGrabber.EUGameGrabTask;
import GameGrabber.Game;
import GameGrabber.GameLab;
import GameGrabber.JPGameGrabTask;
import GameGrabber.RatesQueryTask;
import GameGrabber.USGameGrabTask;
import Util.QueryPreferences;

public class MainActivity extends AppCompatActivity {
    private static boolean STRICT_MODE = false;

    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private List<Game> mGames;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
            prefs.edit().putInt("version", currentVersion).commit();
        }

        DownloadListener listener = new DownloadListener() {
            @Override
            public void onSuccess() { }
            @Override
            public void onFailed() { }
        };
        RatesQueryTask ratesQueryTask = new RatesQueryTask(this, listener);
        ratesQueryTask.execute("CNY");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setProgressViewEndTarget(false, 50);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        mGames = GameLab.get(this).getGames();
        ViewPreloadSizeProvider<Game> sizeProvider = new ViewPreloadSizeProvider<>();
        ListPreloader.PreloadModelProvider modelProvider = new MyPreloadModelProvider();
        RecyclerViewPreloader<Game> preLoader = new RecyclerViewPreloader<>(Glide.with(this), modelProvider, sizeProvider, 100);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.addOnScrollListener(preLoader);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation()));
        GameAdapter adapter = new GameAdapter(this, mGames);
        mRecyclerView.setAdapter(adapter);

        /**
         *  Below here is test code
         */
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fab.setVisibility(View.INVISIBLE);

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
        public List<String> getPreloadItems(int position) {
            String url = mGames.get(position).getIconUrl();
            if (TextUtils.isEmpty(url)) {
                return Collections.emptyList();
            }
            return Collections.singletonList(url);
        }

        @Override
        public RequestBuilder getPreloadRequestBuilder(Object item) {
            return GlideApp.with(mContext)
                            .load((String) item);
        }
    }

    private void refreshData() {
        DownloadListener listener = new DownloadListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(mContext, "Success!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
            }
        };

        final USGameGrabTask usGameGrabTask = new USGameGrabTask(mContext, listener);
        final EUGameGrabTask euGameGrabTask = new EUGameGrabTask(mContext, listener);
        final JPGameGrabTask jpGameGrabTask = new JPGameGrabTask(mContext, listener);
        final RatesQueryTask ratesQueryTask = new RatesQueryTask(mContext, listener);

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
                        List<Game> games = GameLab.get(mContext).getGames();
                        GameAdapter adapter = new GameAdapter(mContext, games);
                        mRecyclerView.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(mContext);
        List<Game> games;
        if (query == null) {
            games = GameLab.get(mContext).getGames();
        } else {
            games = new ArrayList<>();
            for (Game game : mGames) {
                if (game.getUsTitle().toLowerCase().contains(query.toLowerCase())) {
                    games.add(game);
                }
            }
        }
        GameAdapter adapter = new GameAdapter(mContext, games);
        mRecyclerView.setAdapter(adapter);
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
