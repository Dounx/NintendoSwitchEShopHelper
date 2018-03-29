package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import GameGrabber.DownloadListener;
import GameGrabber.Game;
import GameGrabber.GameLab;
import GameGrabber.PriceQueryTask;
import GameGrabber.SupportedCountry;
import GameGrabber.SupportedCountryLab;
import Util.DateFormatter;
import Util.QueryPreferences;
import Util.UserPreferences;

public class GamePageActivity extends AppCompatActivity {
    public static final String EXTRA_GAME_TITLE = "me.dounx.android.nintendoeshophelper.game_title";
    private PriceQueryTask mPriceQueryTask;
    private Game mGame;
    private Context mContext;

    public static Intent  newIntent(Context packageContext, String title) {
        Intent intent = new Intent(packageContext, GamePageActivity.class);
        intent.putExtra(EXTRA_GAME_TITLE, title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(R.layout.activity_game_page);
        mContext = this;

        String title = (String)getIntent().getSerializableExtra(EXTRA_GAME_TITLE);

        Toolbar toolbar = findViewById(R.id.game_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGame = GameLab.get(this).getGame(title);


        getSupportActionBar().setTitle(mGame.getTitle());

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
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_loading)
                .into(gamePageImage);

        if (mGame.isDiscount()) {
            TextView gamePageDiscountPrice = findViewById(R.id.game_page_discount_price);
            TextView gamePageDiscountNum = findViewById(R.id.game_page_discount_num);
            TextView gamePageStartTime = findViewById(R.id.game_page_start_time);
            TextView gamePageEndTime = findViewById(R.id.game_page_end_time);
            LinearLayout gamePageDiscountTime = findViewById(R.id.game_page_discount_time);

            gamePageDiscountPrice.setVisibility(View.VISIBLE);
            gamePageDiscountNum.setVisibility(View.VISIBLE);
            gamePageStartTime.setVisibility(View.VISIBLE);
            gamePageEndTime.setVisibility(View.VISIBLE);
            gamePageDiscountTime.setVisibility(View.VISIBLE);
        }

        TextView gamePageReleaseDate = findViewById(R.id.game_page_release_date);

        gamePageReleaseDate.setText(DateFormatter.ParseDateToString(mGame.getReleaseDate(), Locale.getDefault(), mContext));

        TextView gamePageLanguage = findViewById(R.id.game_page_language);
        gamePageLanguage.setText(mGame.getLanguage());
        TextView gamePagePlayerNumber = findViewById(R.id.game_page_player_number);
        gamePagePlayerNumber.setText(mGame.getPlayerNumber());

        TextView gamePageCategory = findViewById(R.id.game_page_category);
        gamePageCategory.setText(mGame.getCategory());

        final TextView gamePagePrice = findViewById(R.id.game_page_price);
        final TextView gamePageCountry = findViewById(R.id.game_page_country);
        final ProgressBar progressBar = findViewById(R.id.price_progress_bar);
        final TextView progress_info = findViewById(R.id.progress_info);
        final TextView gamePageStartTime = findViewById(R.id.game_page_start_time);
        final TextView gamePageEndTime = findViewById(R.id.game_page_end_time);

        final List<SupportedCountry> list = SupportedCountryLab.get(this).getSupportedCountries();
        progressBar.setMax(list.size());

        DownloadListener listener = new DownloadListener() {
            @Override
            public void onSuccess() {
                if (mGame.isDiscount()) {
                    TextView gamePageDiscountPrice = findViewById(R.id.game_page_discount_price);
                    TextView gamePageDiscountNum = findViewById(R.id.game_page_discount_num);
                    TextView gamePageStartTime = findViewById(R.id.game_page_start_time);
                    TextView gamePageEndTime = findViewById(R.id.game_page_end_time);
                    TextView gamePagePrice = findViewById(R.id.game_page_price);
                    LinearLayout gamePageDiscountTime = findViewById(R.id.game_page_discount_time);

                    if (mGame.getPrice().getStartTime() != null) {
                        gamePagePrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );    // Add a line in it
                        gamePageDiscountPrice.setText(mGame.getPrice().getDiscountPriceByCurrency() + " " + UserPreferences.getStoredCurrency(mContext));
                        gamePageDiscountNum.setText(mGame.getPrice().getDiscount() + getString(R.string.off));

                        Locale locale;
                        if (UserPreferences.getStoredLanguage(mContext).equals("Chinese")) {
                            locale = Locale.CHINA;
                        } else {
                            locale = Locale.US;
                        }
                        gamePageStartTime.setText(DateFormatter.ParseDateToString(mGame.getPrice().getStartTime(), locale, mContext));
                        gamePageEndTime.setText(DateFormatter.ParseDateToString(mGame.getPrice().getEndTime(), locale, mContext));
                    } else {
                        gamePageDiscountTime.setVisibility(View.GONE);
                        gamePageStartTime.setVisibility(View.GONE);
                        gamePageEndTime.setVisibility(View.GONE);
                        gamePageDiscountNum.setVisibility(View.GONE);
                        gamePageDiscountPrice.setVisibility(View.GONE);
                        Toast.makeText(mContext, getString(R.string.discount_useless), Toast.LENGTH_LONG).show();
                    }
                }

                gamePagePrice.setText(mGame.getPrice().getPriceByCurrency() + " " + UserPreferences.getStoredCurrency(mContext));
                gamePageCountry.setText(mGame.getPrice().getCountryName());
                progressBar.setVisibility(View.GONE);
                progress_info.setVisibility(View.GONE);
            }
            @Override
            public void onFailed() {
                gamePagePrice.setText(R.string.failed);
                gamePageCountry.setText(R.string.failed);
                gamePageStartTime.setText(R.string.failed);
                gamePageEndTime.setText(R.string.failed);
                if (mGame.getGameCode() == null || mGame.getUsNsUid() == null && (mGame.getEuNsUid() == null && mGame.getJpNsUid() == null)) {
                    Toast.makeText(mContext, getString(R.string.not_exist), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, getString(R.string.network_error), Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
                progress_info.setVisibility(View.GONE);
            }
        };

        mPriceQueryTask = new PriceQueryTask(this, listener, progressBar, progress_info);
        mPriceQueryTask.execute(mGame);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPriceQueryTask.getStatus() == AsyncTask.Status.RUNNING) {
            mPriceQueryTask.cancel(true);
        }
        ActivityCollector.removeActivity(this);
    }
}
