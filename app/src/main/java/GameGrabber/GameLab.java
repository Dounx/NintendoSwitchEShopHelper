package GameGrabber;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import GameDbSchema.GameBaseHelper;
import GameDbSchema.GameCursorWrapper;

import static GameDbSchema.GameDbSchema.*;

/**
 * Singleton pattern for Game class
 */
public class GameLab {
    private static GameLab sGameLab;
    private final Context mContext;
    private final SQLiteDatabase mDatabase;
    public HashMap<String, Double> RatesMap;

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
        return games;
    }

    // Close the database connection
    public void Clean() {
        if (mDatabase.isOpen()) {
            mDatabase.close();
        }
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
}
