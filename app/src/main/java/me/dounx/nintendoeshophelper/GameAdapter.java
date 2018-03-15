package me.dounx.nintendoeshophelper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import GameGrabber.Game;

/**
 * Created by Dounx on 2018/3/16.
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private List<Game> mGameList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView gameName;

        public ViewHolder(View view) {
            super(view);
            gameName = view.findViewById(R.id.gameName);
        }
    }

    public GameAdapter(List<Game> gameList) {
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
    }

    @Override
    public int getItemCount() {
        return mGameList.size();
    }
}
