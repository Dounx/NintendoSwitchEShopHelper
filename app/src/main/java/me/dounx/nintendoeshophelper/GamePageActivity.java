package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import GameGrabber.DownloadListener;
import GameGrabber.Game;
import GameGrabber.GameLab;
import GameGrabber.PriceQueryTask;
import GameGrabber.SupportedCountry;
import GameGrabber.SupportedCountryLab;

public class GamePageActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_TITLE = "me.dounx.android.nintendoeshophelper.game_title";
    private PriceQueryTask mPriceQueryTask;
    private Game mGame;

    public static Intent  newIntent(Context packageContext, String title) {
        Intent intent = new Intent(packageContext, GamePageActivity.class);
        intent.putExtra(EXTRA_GAME_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        String title = (String)getIntent().getSerializableExtra(EXTRA_GAME_TITLE);

        Toolbar toolbar = findViewById(R.id.game_page_toolbar);
        setSupportActionBar(toolbar);

        mGame = GameLab.get(this).getGame(title);

        getSupportActionBar().setTitle(mGame.getUsTitle());

        ImageView gamePageImage = findViewById(R.id.game_page_image);
        gamePageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mGame.getUrl()));
                startActivity(intent);
            }
        });
        GlideApp.with(this)
                .load(mGame.getIconUrl())
                .error(R.drawable.ic_no_pic)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_loading)
                .into(gamePageImage);

        TextView gamePageReleaseDate = findViewById(R.id.game_page_release_date);
        gamePageReleaseDate.setText(mGame.getReleaseDate());
        TextView gamePageLanguage = findViewById(R.id.game_page_language);
        gamePageLanguage.setText(mGame.getLanguage());
        TextView gamePagePlayerNumber = findViewById(R.id.game_page_player_number);
        gamePagePlayerNumber.setText(mGame.getPlayerNumber());
        TextView gamePageDiscount = findViewById(R.id.game_page_discount);
        gamePageDiscount.setText(mGame.isDiscount()? "Have discount" : "");
        TextView gamePageCategory = findViewById(R.id.game_page_category);
        gamePageCategory.setText(mGame.getCategory());

        final TextView gamePagePrice = findViewById(R.id.game_page_price);
        final TextView gamePageCountry = findViewById(R.id.game_page_country);
        final ProgressBar progressBar = findViewById(R.id.price_progress_bar);
        final TextView progress_info = findViewById(R.id.progress_info);

        final List<SupportedCountry> list = SupportedCountryLab.get(this).getSupportedCountries();
        progressBar.setMax(list.size());

        DownloadListener listener = new DownloadListener() {
            @Override
            public void onSuccess() {
                gamePagePrice.setText(mGame.getPrice().getPrice() + " CNY ");
                gamePageCountry.setText(mGame.getPrice().getCountryName());
                progressBar.setVisibility(View.INVISIBLE);
                progress_info.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailed() {
                gamePagePrice.setText(R.string.failed);
                gamePageCountry.setText(R.string.failed);
                progressBar.setVisibility(View.INVISIBLE);
                progress_info.setVisibility(View.INVISIBLE);
            }
        };

        if (mGame.getPrice() == null) {
            mPriceQueryTask = new PriceQueryTask(this, listener, progressBar, progress_info);
            mPriceQueryTask.execute(mGame);
        } else {
            gamePagePrice.setText(mGame.getPrice().getPrice() + " CNY ");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPriceQueryTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPriceQueryTask.cancel(true);
        }
    }
}
