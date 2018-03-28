package GameGrabber;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import GameDbSchema.GameBaseHelper;
import GameDbSchema.GameCursorWrapper;
import Util.DateFormatter;

import static GameDbSchema.GameDbSchema.*;

/**
 * Singleton pattern for Game class
 */
public class GameLab {
    private static GameLab sGameLab;
    private final Context mContext;
    private final SQLiteDatabase mDatabase;
    public HashMap<String, Double> mRatesMap;

    public static GameLab get(Context context) {
        if (sGameLab == null) {
            sGameLab = new GameLab(context);
        }
        return sGameLab;
    }

    private GameLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new GameBaseHelper(mContext).getReadableDatabase();
    }

    public List<Game> getGames() {
        List<Game> games = new ArrayList<>();

        try (GameCursorWrapper cursor = queryGames(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                games.add(cursor.getGame());
                cursor.moveToNext();
            }
        }
        DealWithSpecialGameData(games);
        return games;
    }

    public Game getGame(String title) {
        List<Game> games = getGames();
        for (Game game : games) {
            if (game.getTitle().equals(title)) {
                return game;
            }
        }
        return null;
    }

    public List<Game> getGamesByReleaseDate(final boolean isAsc) {
        List<Game> games = getGames();

        Collections.sort(games,new Comparator<Game>(){
            public int compare(Game arg0, Game arg1) {
                DateFormatter formatter = new DateFormatter();
                Date date0 = arg0.getReleaseDate();
                Date date1 = arg1.getReleaseDate();

                return isAsc? date0.compareTo(date1) : date1.compareTo(date0);
            }
        });

        return games;
    }

    // Not released games
    public List<Game> getNewGames() {
        List<Game> games = getGames();
        List<Game> newGames = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        for (Game game : games) {
            Date date = game.getReleaseDate();
            dates.add(date);
        }

        Date currentDate = Calendar.getInstance(Locale.US).getTime();

        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i) != null && dates.get(i).after(currentDate)) {
                newGames.add(games.get(i));
            }
        }
        return newGames;
    }

    public List<Game> getDiscountGames() {
        List<Game> games = getGames();
        List<Game> discountGames = new ArrayList<>();

        for (Game game : games) {
            if (game.isDiscount()) {
                discountGames.add(game);
            }
        }
        return discountGames;
    }

    private GameCursorWrapper queryGames(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                GameView.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new GameCursorWrapper(cursor);
    }

    // Special game have multi-version need to deal with
    private void DealWithSpecialGameData(List<Game> games) {
        Iterator<Game> iterator = games.iterator();

        while (iterator.hasNext()) {
            Game game = iterator.next();
            // NBA 2K18
            if (game.getGameCode() != null && game.getGameCode().equals("AB38")) {
                if (game.getUsTitle().equals("NBA 2K18") && game.getJpTitle().equals("NBA 2K18")) {
                }
                if (game.getUsTitle().equals("NBA 2K18 Legend Edition") && game.getJpTitle().equals("NBA 2K18 レジェンド エディション")) {
                }
                if (game.getUsTitle().equals("NBA 2K18 Legend Edition Gold") && game.getJpTitle().equals("NBA 2K18 レジェンド エディション ゴールド")) {
                }
                iterator.remove();
            }
        }
    }
}
