package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import GameGrabber.DownloadListener;
import GameGrabber.Game;
import GameGrabber.PriceQueryTask;
import Util.GlideApp;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<Game> mGameList;
    private Context mContext;
    private int mPass = 0;
    private int mTaskCount = 0;
    private static final int LIMIT = 3;
    private DownloadListener mListener = new DownloadListener() {
        @Override
        public void onSuccess() {
            mTaskCount--;
        }

        @Override
        public void onFailed() {
            mTaskCount--;
        }
    };

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        TextView gameName;
        TextView gamePrice;
        TextView gameCategory;


        public ViewHolder(View view) {
            super(view);
            gameImage = view.findViewById(R.id.game_image);
            gameName = view.findViewById(R.id.game_name);
            gamePrice = view.findViewById(R.id.game_price);
            gameCategory = view.findViewById(R.id.game_category);
        }
    }

    public GameAdapter(Context context, List<Game> gameList) {
        mContext = context;
        mGameList = gameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Game game = mGameList.get(position);

        holder.gameName.setText(game.getUsTitle());
        holder.gameCategory.setText(game.getCategory());

        if (position > mPass && mTaskCount < LIMIT){
            PriceQueryTask priceQueryTask = new PriceQueryTask(mContext, mListener, this, mGameList, position);
            priceQueryTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mPass = position;
            mTaskCount++;
        }

        if (game.getPrice() != null) {
            holder.gamePrice.setText(game.getPrice().getPrice() + "CNY" + " (" + game.getPrice().getCountryName() + ")");  // Use CNY to test it
        }

        GlideApp.with(mContext)
                .load(game.getIconUrl())
                .into(holder.gameImage);
    }

    @Override
    public int getItemCount() {
        return mGameList.size();
    }
}
