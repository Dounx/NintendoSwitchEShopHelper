package GameGrabber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import GameDbSchema.GameBaseHelper;
import GameDbSchema.GameDbSchema.USGameTable;
import GameDbSchema.USGameCursorWrapper;
import Util.ConvertBetweenStringAndStringArray;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Request all the games and the respond is a json style
 */
public class USGameGrabTask extends AsyncTask<String, Integer, Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final int LIMIT = 200;

    private final Context mContext;
    private final SQLiteDatabase mDatabase;

    public USGameGrabTask(Context context) {
        this.mContext = context;
        this.mDatabase = new GameBaseHelper(mContext).getWritableDatabase();
    }

    @Override
    protected Integer doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();
        int offset = 0;
        int total = 1;

        //// We can grab 200 games info with a request
        while (offset < total + LIMIT) {    // Need to grab the lase games, so just plus LIMIT and finally total = -1
            HttpUrl httpUrl = new HttpUrl.Builder()
                    .scheme("https")
                    .host("nintendo.com")
                    .addPathSegment("json")
                    .addPathSegment("content")
                    .addPathSegment("get")
                    .addPathSegment("filter")
                    .addPathSegment("game")
                    .addQueryParameter("system", "switch")
                    .addQueryParameter("sort", "title")
                    .addQueryParameter("direction", "c")
                    .addQueryParameter("shop", "ncom")
                    .addQueryParameter("limit", Integer.toString(LIMIT))
                    .addQueryParameter("offset", Integer.toString(offset))
                    .build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String responseData = response.body().string();
                if (!responseData.equals("{}")) {
                    total = parseJsonWithJSONObjectAndAddToDB(responseData);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return TYPE_FAILED;
            }
            offset += LIMIT;
        }
        return TYPE_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                break;
            case TYPE_FAILED:
                break;
            default:
        }

        // Close the database connection
        mDatabase.close();
    }

    private int parseJsonWithJSONObjectAndAddToDB(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            JSONObject gamesObject = jsonObject.getJSONObject("games");

            if (gamesObject.has("game")) {  // Last time may not have game array
                JSONArray gameArray = gamesObject.getJSONArray("game");
                for (int i = 0; i < gameArray.length(); i++) {
                    USGame usGame = new USGame();
                    JSONObject gameObject = gameArray.getJSONObject(i);
                    JSONObject categoriesObject = gameObject.getJSONObject("categories");

                    try {
                        JSONArray categoryArray = categoriesObject.getJSONArray("category");
                        String[] categories = new String[categoryArray.length()];

                        for (int j = 0; j < categoryArray.length(); j++) {
                            categories[j] = categoryArray.getString(j);
                        }
                        usGame.setCategory(ConvertBetweenStringAndStringArray.convertArrayToString(categories));
                    } catch (Exception e) {
                        String category = categoriesObject.getString("category");
                        usGame.setCategory(category);
                    }

                    usGame.setSlug(gameObject.getString("slug"));
                    usGame.setBuyItNow(gameObject.getBoolean("buyitnow"));
                    usGame.setReleaseData(gameObject.getString("release_date"));
                    usGame.setDigitalDownload(gameObject.getBoolean("digitaldownload"));
                    usGame.setFreeToStart(gameObject.getBoolean("free_to_start"));
                    usGame.setTitle(gameObject.getString("title"));
                    usGame.setSystem(gameObject.getString("system"));
                    usGame.setId(gameObject.getString("id"));
                    if (gameObject.has("ca_price")) {
                        usGame.setCaPrice(gameObject.getString("ca_price"));
                    }
                    usGame.setNumberOfPlayers(gameObject.getString("number_of_players"));
                    if (gameObject.has("nsuid")) {
                        usGame.setNsUid(gameObject.getString("nsuid"));
                    }
                    if (gameObject.has("video_link")) {
                        usGame.setVideoLink(gameObject.getString("video_link"));
                    }
                    if (gameObject.has("eshop_price")) {
                        usGame.setEShopPrice(gameObject.getString("eshop_price"));
                    }
                    usGame.setFrontBoxArt(gameObject.getString("front_box_art"));
                    usGame.setGameCode(parseGameCode(gameObject.getString("game_code")));
                    usGame.setBuyOnline(gameObject.getBoolean("buyonline"));

                    addUSGame(usGame);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void addUSGame(USGame usGame) {
        ContentValues values = getContentValues(usGame);

        USGameCursorWrapper cursor = queryUSGames("title = ?", new String[]{usGame.getTitle()});

        // If exist, just update info, else insert to it
        if (cursor.moveToFirst()) {
            mDatabase.update(USGameTable.NAME, values, "title = ?", new String[]{usGame.getTitle()});
        } else{
            mDatabase.insert(USGameTable.NAME, null, values);
        }
        cursor.close();
    }

    private USGameCursorWrapper queryUSGames(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                USGameTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new USGameCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(USGame usGame) {
        ContentValues values = new ContentValues();
        values.put(USGameTable.Cols.NSUID, usGame.getNsUid());
        values.put(USGameTable.Cols.CATEGORY, usGame.getCategory());
        values.put(USGameTable.Cols.SLUG, usGame.getSlug());
        values.put(USGameTable.Cols.BUYITNOW, usGame.isBuyItNow());
        values.put(USGameTable.Cols.RELEASE_DATE, usGame.getReleaseData());
        values.put(USGameTable.Cols.DIGITALDOWNLOAD, usGame.isDigitalDownload());
        values.put(USGameTable.Cols.FREE_TO_START, usGame.isFreeToStart());
        values.put(USGameTable.Cols.TITLE, usGame.getTitle());
        values.put(USGameTable.Cols.SYSTEM, usGame.getSystem());
        values.put(USGameTable.Cols.ID, usGame.getId());
        values.put(USGameTable.Cols.CA_PRICE, usGame.getCaPrice());
        values.put(USGameTable.Cols.NUMBER_OF_PLAYERS, usGame.getNumberOfPlayers());
        values.put(USGameTable.Cols.VIDEO_LINK, usGame.getVideoLink());
        values.put(USGameTable.Cols.ESHOP_PRICE, usGame.getEShopPrice());
        values.put(USGameTable.Cols.FRONT_BOX_ART, usGame.getFrontBoxArt());
        values.put(USGameTable.Cols.GAME_CODE, usGame.getGameCode());
        values.put(USGameTable.Cols.BUYONLINE, usGame.isBuyOnline());

        return values;
    }

    /**
     * Parse Game Code
     * @param gameCode A complete 9 game code ( special like HAC is 3 digit-code ), but we need just 4-digit ( For linking USGame, EUGame and JPGame table )
     * @return Parsed 4-digit game code
     */
    private String parseGameCode(String gameCode) {
        return gameCode.length() > 3? gameCode.substring(4, 8): null;    // 3 digit-code because of code HAC ( Just HAC! )
    }
}
