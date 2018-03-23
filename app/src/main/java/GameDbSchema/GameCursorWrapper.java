package GameDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import GameGrabber.Game;
import Util.DateFormatter;

import static GameDbSchema.GameDbSchema.*;

public class GameCursorWrapper extends CursorWrapper {
    public GameCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Game getGame() {
        String us_title = getString(getColumnIndex(GameView.Cols.US_TITLE));
        String eu_title = getString(getColumnIndex(GameView.Cols.EU_TITLE));
        String jp_title = getString(getColumnIndex(GameView.Cols.JP_TITLE));

        String game_code = null;
        if (!isNull(getColumnIndex(GameView.Cols.GAME_CODE0))) {
            game_code = getString(getColumnIndex(GameView.Cols.GAME_CODE0));
        } else if (!isNull(getColumnIndex(GameView.Cols.GAME_CODE1))) {
            game_code = getString(getColumnIndex(GameView.Cols.GAME_CODE1));
        } else if (!isNull(getColumnIndex(GameView.Cols.GAME_CODE2))) {
            game_code = getString(getColumnIndex(GameView.Cols.GAME_CODE2));
        }

        String language = getString(getColumnIndex(GameView.Cols.LANGUAGE));
        String us_nsuid = getString(getColumnIndex(GameView.Cols.US_NSUID));
        String eu_nsuid = getString(getColumnIndex(GameView.Cols.EU_NSUID));
        String jp_nsuid = getString(getColumnIndex(GameView.Cols.JP_NSUID));
        String discount = getString(getColumnIndex(GameView.Cols.DISCOUNT));

        String icon_url = null;
        if (!isNull(getColumnIndex(GameView.Cols.ICON_URL0))) {
            icon_url= getString(getColumnIndex(GameView.Cols.ICON_URL0));
        } else if (!isNull(getColumnIndex(GameView.Cols.ICON_URL1))) {
            icon_url= getString(getColumnIndex(GameView.Cols.ICON_URL1));
        }

        String url = null;
        if (!isNull(getColumnIndex(GameView.Cols.URL0))) {
            url = "https://www.nintendo.com/games/detail/" + getString(getColumnIndex(GameView.Cols.URL0));
        } else if (!isNull(getColumnIndex(GameView.Cols.URL1))) {
            url = "https://www.nintendo.co.uk" + getString(getColumnIndex(GameView.Cols.URL1));
        } else if (!isNull(getColumnIndex(GameView.Cols.URL2))) {
            url = getString(getColumnIndex(GameView.Cols.URL2));
        }

        String release_date = null;
        if (!isNull(getColumnIndex(GameView.Cols.RELEASE_DATE0))) {
            release_date = getString(getColumnIndex(GameView.Cols.RELEASE_DATE0));
        } else if (!isNull(getColumnIndex(GameView.Cols.RELEASE_DATE1))) {
            release_date = getString(getColumnIndex(GameView.Cols.RELEASE_DATE1));
        } else if (!isNull(getColumnIndex(GameView.Cols.RELEASE_DATE2))) {
            release_date = getString(getColumnIndex(GameView.Cols.RELEASE_DATE2));
        }

        String player_number = null;
        if (!isNull(getColumnIndex(GameView.Cols.PLAYER_NUMBER0))) {
            player_number = getString(getColumnIndex(GameView.Cols.PLAYER_NUMBER0));
        } else if (!isNull(getColumnIndex(GameView.Cols.PLAYER_NUMBER1))) {
            player_number = getString(getColumnIndex(GameView.Cols.PLAYER_NUMBER1));
        }

        String category = null;
        if (!isNull(getColumnIndex(GameView.Cols.CATEGORY0))) {
            category = getString(getColumnIndex(GameView.Cols.CATEGORY0));
        } else if (!isNull(getColumnIndex(GameView.Cols.CATEGORY1))) {
            category = getString(getColumnIndex(GameView.Cols.CATEGORY1));
        }

        Game game = new Game();
        game.setUsTitle(us_title);
        game.setEuTitle(eu_title);
        game.setJpTitle(jp_title);
        game.setGameCode(game_code);
        game.setLanguage(language);
        game.setUsNsUid(us_nsuid);
        game.setEuNsUid(eu_nsuid);
        game.setJpNsUid(jp_nsuid);

        // null and 0 equal false
        if (discount == null) {
            game.setDiscount(false);
        } else {
            game.setDiscount(discount.equals("1"));
        }

        game.setIconUrl(icon_url);
        game.setUrl(url);

        DateFormatter formatter = new DateFormatter();
        game.setReleaseDate(formatter.ParseStringToDate(release_date));

        game.setPlayerNumber(player_number);
        game.setCategory(category);

        return game;
    }
}
