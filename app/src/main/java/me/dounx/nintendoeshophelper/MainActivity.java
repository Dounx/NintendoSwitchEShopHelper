package me.dounx.nintendoeshophelper;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import GameGrabber.Game;
import GameGrabber.GameLab;
import Util.GlideApp;

public class MainActivity extends AppCompatActivity {
    private static boolean STRICT_MODE = false;

    private DrawerLayout mDrawerLayout;
    private List<Game> mGames;
    private GameAdapter mAdapter;

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

        mGames = GameLab.get(this).getGames();
        ViewPreloadSizeProvider<Game> sizeProvider = new ViewPreloadSizeProvider<>();
        ListPreloader.PreloadModelProvider modelProvider = new MyPreloadModelProvider();
        RecyclerViewPreloader<Game> preLoader = new RecyclerViewPreloader<>(Glide.with(this), modelProvider, sizeProvider, 100);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(preLoader);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        mAdapter = new GameAdapter(this, mGames);
        recyclerView.setAdapter(mAdapter);


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
                List<Game> games = new ArrayList<>();
                for (Game game : mGames) {
                    if (game.getUsTitle().contains(query)) {
                        games.add(game);
                    }
                }
                mGames = games;
                mAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                onQueryTextSubmit(newText);
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
            return GlideApp.with(getApplication())
                            .load((String) item);
        }
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
