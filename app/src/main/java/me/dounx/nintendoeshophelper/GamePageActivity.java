package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import GameGrabber.DownloadListener;
import GameGrabber.Game;
import GameGrabber.GameLab;
import GameGrabber.PriceQueryTask;

public class GamePageActivity extends AppCompatActivity {

    public static final String EXTRA_GAME_CODE = "me.dounx.android.nintendoeshophelper.game_code";
    private Game mGame;

    public static Intent  newIntent(Context packageContext, String gameCode) {
        Intent intent = new Intent(packageContext, GamePageActivity.class);
        intent.putExtra(EXTRA_GAME_CODE, gameCode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);
        String gameCode = (String)getIntent().getSerializableExtra(EXTRA_GAME_CODE);
        mGame = GameLab.get(this).getGame(gameCode);

        ImageView gamePageImage = findViewById(R.id.game_page_image);

        GlideApp.with(this)
                .load(mGame.getIconUrl())
                .error(R.drawable.ic_no_pic)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_loading)
                .into(gamePageImage);

        final TextView gamePagePrice = findViewById(R.id.game_page_price);

        DownloadListener listener = new DownloadListener() {
            @Override
            public void onSuccess() {
                gamePagePrice.setText(mGame.getPrice().getPrice() + " CNY " + "(" + mGame.getPrice().getCountryName() + ")");
            }
            @Override
            public void onFailed() {
            }
        };

        final PriceQueryTask priceQueryTask = new PriceQueryTask(this, listener);
        priceQueryTask.execute(mGame);
    }
}
