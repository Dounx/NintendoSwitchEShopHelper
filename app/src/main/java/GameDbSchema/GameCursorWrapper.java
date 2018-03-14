package GameDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import GameGrabber.Game;

import static GameDbSchema.GameDbSchema.*;

public class GameCursorWrapper extends CursorWrapper {
    public GameCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Game getGame() {
        String us_title = getString(getColumnIndex(GameView.Cols.US_TITLE));
        String eu_title = getString(getColumnIndex(GameView.Cols.EU_TITLE));
        String jp_title = getString(getColumnIndex(GameView.Cols.JP_TITLE));
        String game_code = getString(getColumnIndex(GameView.Cols.GAME_CODE));
        String language = getString(getColumnIndex(GameView.Cols.LANGUAGE));
        String us_nsuid = getString(getColumnIndex(GameView.Cols.US_NSUID));
        String eu_nsuid = getString(getColumnIndex(GameView.Cols.EU_NSUID));
        String jp_nsuid = getString(getColumnIndex(GameView.Cols.JP_NSUID));
        String discount = getString(getColumnIndex(GameView.Cols.DISCOUNT));
        String icon_url = getString(getColumnIndex(GameView.Cols.ICON_URL));
        String url = getString(getColumnIndex(GameView.Cols.URL));
        String release_date = getString(getColumnIndex(GameView.Cols.RELEASE_DATE));
        String player_number = getString(getColumnIndex(GameView.Cols.PLAYER_NUMBER));
        String category = getString(getColumnIndex(GameView.Cols.CATEGORY));

        Game game = new Game();
        game.setUsTitle(us_title);
        game.setEuTitle(eu_title);
        game.setJpTitle(jp_title);
        game.setGameCode(game_code);
        game.setLanguage(language);
        game.setUsNsUid(us_nsuid);
        game.setEuNsUid(eu_nsuid);
        game.setJpNsUid(jp_nsuid);
        game.setDiscount(discount.equals("1"));    // null and 0 equal false
        game.setIconUrl(icon_url);
        game.setUrl(url);
        game.setReleaseDate(release_date);
        game.setPlayerNumber(player_number);
        game.setCategory(category);

        return game;
    }
}
