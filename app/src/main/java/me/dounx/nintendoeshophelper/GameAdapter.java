package me.dounx.nintendoeshophelper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Locale;

import GameGrabber.Game;
import Util.DateFormatter;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<Game> mGameList;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView gameImage;
        TextView gameName;
        TextView gameReleaseDate;
        TextView gameCategory;


        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            gameImage = view.findViewById(R.id.game_image);
            gameName = view.findViewById(R.id.game_name);
            gameReleaseDate = view.findViewById(R.id.game_release_date);
            gameCategory = view.findViewById(R.id.game_category);
        }

        @Override
        public void onClick(View view) {
            Game game = mGameList.get((int)view.getTag());

            Intent intent = GamePageActivity.newIntent(mContext, game.getTitle());
            mContext.startActivity(intent);
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
        holder.itemView.setTag(position);
        Game game = mGameList.get(position);

        String title = null;
        if (game.getUsTitle() != null) {
            title = game.getUsTitle();
        } else if (game.getEuNsUid() != null) {
            title = game.getEuTitle();
        } else if (game.getJpTitle() != null) {
            title = game.getJpTitle();
        }

        holder.gameName.setText(title);

        holder.gameReleaseDate.setText(DateFormatter.ParseDateToString(game.getReleaseDate(), Locale.getDefault(), mContext));

        holder.gameCategory.setText(game.getCategory());

        GlideApp.with(mContext)
                .load(game.getIconUrl())
                .error(R.drawable.ic_no_pic)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_loading)
                .into(holder.gameImage);
    }

    @Override
    public int getItemCount() {
        return mGameList.size();
    }

    public void setData(List<Game> newGameList){
        this.mGameList = newGameList;
    }
}
