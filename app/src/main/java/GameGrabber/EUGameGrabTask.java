package GameGrabber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import GameDbSchema.EUGameCursorWrapper;
import GameDbSchema.GameBaseHelper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static GameDbSchema.GameDbSchema.*;

/**
 * Request all the games and the respond is a json style
 * Europe's API can return much useful info
 * Such as IsDiscount
 */

public class EUGameGrabTask  extends AsyncTask<String, Integer, Integer>  {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;
    private static final String OFFSET = "0";
    private static final String LIMIT = "9999";
    private static final String LOCALE = "en";

    private final DownloadListener mListener;
    private final Context mContext;
    private final SQLiteDatabase mDatabase;

    public EUGameGrabTask(Context context, DownloadListener listener) {
        this.mListener = listener;
        this.mContext = context;
        this.mDatabase = new GameBaseHelper(mContext).getWritableDatabase();
    }

    @Override
    protected Integer doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();

        //// We can grab all the games info with a request
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host("search.nintendo-europe.com")
                .addPathSegment(LOCALE)
                .addPathSegment("select")
                .addQueryParameter("fq", "type:GAME AND system_type:nintendoswitch* AND product_code_txt:*")
                .addQueryParameter("q", "*")
                .addQueryParameter("sort", "sorting_title asc")
                .addQueryParameter("wt", "json")
                .addQueryParameter("rows", LIMIT)
                .addQueryParameter("start", OFFSET)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            parseJsonWithJSONObjectAndAddToDB(responseData);
        } catch (Exception e) {
            e.printStackTrace();
            return TYPE_FAILED;
        }
        return TYPE_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                mListener.onSuccess();
                break;
            case TYPE_FAILED:
                mListener.onFailed();
                break;
            default:
        }
        // Close the database connection
        mDatabase.close();
    }

    private void parseJsonWithJSONObjectAndAddToDB(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject responseObject = jsonObject.getJSONObject("response");

            int gameCount = responseObject.getInt("numFound");

            try (EUGameCursorWrapper cursor = queryEUGames(null, null )) {
                if (cursor.getCount() == gameCount) {
                    return;
                }
            }

            JSONArray docsArray = responseObject.getJSONArray("docs");

            for (int i = 0; i < docsArray.length(); i++) {
                EUGame euGame = new EUGame();
                JSONObject gameObject = docsArray.getJSONObject(i);

                euGame.setFsId(gameObject.getString("fs_id"));
                euGame.setChangeDate(gameObject.getString("change_date"));
                euGame.setUrl(gameObject.getString("url"));
                euGame.setType(gameObject.getString("type"));
                euGame.setClubNintendo(gameObject.getBoolean("club_nintendo"));
                if (gameObject.has("hd_rumble_b")) {
                    euGame.setHdRumbleB(gameObject.getBoolean("hd_rumble_b"));
                }
                if (gameObject.has("multiplayer_mode")) {
                    euGame.setMultiPlayerMode(gameObject.getString("multiplayer_mode"));
                }
                euGame.setPrettyDateS(gameObject.getString("pretty_date_s"));
                if (gameObject.has("play_mode_tv_mode_b")) {
                    euGame.setPlayModeTvModeB(gameObject.getBoolean("play_mode_tv_mode_b"));
                }
                if (gameObject.has("play_mode_handheld_mode_b")) {
                    euGame.setPlayModeHandheldModeB(gameObject.getBoolean("play_mode_handheld_mode_b"));
                }
                euGame.setImageUrlSqS(gameObject.getString("image_url_sq_s"));
                euGame.setPgS(gameObject.getString("pg_s"));
                euGame.setGiftFinderDetailPageImageUrlS(gameObject.getString("gift_finder_detail_page_image_url_s"));
                euGame.setImageUrl(gameObject.getString("image_url"));
                euGame.setOriginallyForT(gameObject.getString("originally_for_t"));
                if (gameObject.has("priority")) {
                    euGame.setPriority(gameObject.getString("priority"));
                }
                euGame.setDigitalVersionB(gameObject.getBoolean("digital_version_b"));
                euGame.setImageUrlH2x1S(gameObject.getString("image_url_h2x1_s"));
                euGame.setAgeRatingSortingI(gameObject.getString("age_rating_sorting_i"));
                if (gameObject.has("play_mode_tabletop_mode_b")) {
                    euGame.setPlayModeTabletopModeB(gameObject.getBoolean("play_mode_tabletop_mode_b"));
                }
                if (gameObject.has("publisher")) {
                    euGame.setPublisher(gameObject.getString("publisher"));
                }
                if (gameObject.has("ir_motion_camera_b")) {
                    euGame.setIrMotionCameraB(gameObject.getBoolean("ir_motion_camera_b"));
                }
                euGame.setExcerpt(gameObject.getString("excerpt"));
                euGame.setDateFrom(gameObject.getString("date_from"));
                if (gameObject.has("price_has_discount_b")) {
                    euGame.setPriceHasDiscountB(gameObject.getBoolean("price_has_discount_b"));
                }
                if (gameObject.has("gift_finder_description_s")) {
                    euGame.setGiftFinderDescriptionS(gameObject.getString("gift_finder_description_s"));
                }
                euGame.setTitle(gameObject.getString("title"));
                euGame.setSortingTitle(gameObject.getString("sorting_title"));
                if (gameObject.has("copyright_s")) {
                    euGame.setCopyrightS(gameObject.getString("copyright_s"));
                }
                euGame.setGiftFinderCarouselImageUrlS(gameObject.getString("gift_finder_carousel_image_url_s"));
                euGame.setPlayersTo(gameObject.getString("players_to"));
                euGame.setGiftFinderWishListImageUrlS(gameObject.getString("gift_finder_wishlist_image_url_s"));
                euGame.setPrettyAgeRatingS(gameObject.getString("pretty_agerating_s"));
                if (gameObject.has("players_from")) {
                    euGame.setPlayersFrom(gameObject.getString("players_from"));
                }
                euGame.setAgeRatingType(gameObject.getString("age_rating_type"));
                if (gameObject.has("gift_finder_detail_page_store_link_s")) {
                    euGame.setGiftFinderDetailPageStoreLinkS(gameObject.getString("gift_finder_detail_page_store_link_s"));
                }
                euGame.setPriceSortingF(gameObject.getString("price_sorting_f"));
                euGame.setPriceLowestF(gameObject.getString("price_lowest_f"));
                euGame.setAgeRatingValue(gameObject.getString("age_rating_value"));
                euGame.setPhysicalVersionB(gameObject.getBoolean("physical_version_b"));

                euGame.setGameCategoriesTxt(gameObject.getJSONArray("game_categories_txt").getString(0));
                euGame.setPlayableOnTxt(gameObject.getJSONArray("playable_on_txt").getString(0));
                euGame.setProductCodeTxt(parseGameCode(gameObject.getJSONArray("product_code_txt").getString(0)));
                euGame.setLanguageAvailability(gameObject.getJSONArray("language_availability").getString(0));
                euGame.setSystemType(gameObject.getJSONArray("system_type").getString(0));
                euGame.setDatesReleasedDts(gameObject.getJSONArray("dates_released_dts").getString(0));
                euGame.setPrettyGameCategoriesTxt(gameObject.getJSONArray("pretty_game_categories_txt").getString(0));
                euGame.setTitleExtrasTxt(gameObject.getJSONArray("title_extras_txt").getString(0));
                if (gameObject.has("nsuid_txt")) {
                    euGame.setNsUidTxt(gameObject.getJSONArray("nsuid_txt").getString(0));
                }
                euGame.setGameCategory(gameObject.getJSONArray("game_category").getString(0));
                euGame.setSystemNamesTxt(gameObject.getJSONArray("system_names_txt").getString(0));

                addEUGame(euGame);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEUGame(EUGame euGame) {
        ContentValues values = getContentValues(euGame);

        EUGameCursorWrapper cursor = queryEUGames("title = ?", new String[]{euGame.getTitle()});

        // If exist, just update info, else insert to it
        if (cursor.moveToFirst()) {
            mDatabase.update(EUGameTable.NAME, values, "title = ?", new String[]{euGame.getTitle()});
        } else{
            mDatabase.insert(EUGameTable.NAME, null, values);
        }
        cursor.close();
    }

    private static ContentValues getContentValues(EUGame euGame) {

        ContentValues values = new ContentValues();
        values.put(EUGameTable.Cols.FS_ID, euGame.getFsId());
        values.put(EUGameTable.Cols.CHANGE_DATE, euGame.getChangeDate());
        values.put(EUGameTable.Cols.URL, euGame.getUrl());
        values.put(EUGameTable.Cols.TYPE, euGame.getType());
        values.put(EUGameTable.Cols.CLUB_NINTENDO, euGame.getClubNintendo());
        values.put(EUGameTable.Cols.HD_RUMBLE_B, euGame.getHdRumbleB());
        values.put(EUGameTable.Cols.MULTIPLAYER_MODE, euGame.getMultiPlayerMode());
        values.put(EUGameTable.Cols.PRETTY_DATE_S, euGame.getPrettyDateS());
        values.put(EUGameTable.Cols.PLAY_MODE_TV_MODE_B, euGame.getPlayModeTvModeB());
        values.put(EUGameTable.Cols.PLAY_MODE_HANDHELD_MODE_B, euGame.getPlayModeHandheldModeB());
        values.put(EUGameTable.Cols.IMAGE_URL_SQ_S, euGame.getImageUrlSqS());
        values.put(EUGameTable.Cols.PG_S, euGame.getPgS());
        values.put(EUGameTable.Cols.GIFT_FINDER_DETAIL_PAGE_IMAGE_URL_S, euGame.getGiftFinderDetailPageImageUrlS());
        values.put(EUGameTable.Cols.IMAGE_URL, euGame.getImageUrl());
        values.put(EUGameTable.Cols.ORIGINALLY_FOR_T, euGame.getOriginallyForT());
        values.put(EUGameTable.Cols.PRIORITY, euGame.getPriority());
        values.put(EUGameTable.Cols.DIGITAL_VERSION_B, euGame.getDigitalVersionB());
        values.put(EUGameTable.Cols.IMAGE_URL_H2X1_S, euGame.getImageUrlH2x1S());
        values.put(EUGameTable.Cols.AGE_RATING_SORTING_I, euGame.getAgeRatingSortingI());
        values.put(EUGameTable.Cols.PLAY_MODE_TABLETOP_MODE_B, euGame.getPlayModeTabletopModeB());
        values.put(EUGameTable.Cols.PUBLISHER, euGame.getPublisher());
        values.put(EUGameTable.Cols.IR_MOTION_CAMERA_B, euGame.getIrMotionCameraB());
        values.put(EUGameTable.Cols.EXCERPT, euGame.getExcerpt());
        values.put(EUGameTable.Cols.DATE_FROM, euGame.getDateFrom());
        values.put(EUGameTable.Cols.PRICE_HAS_DISCOUNT_B, euGame.getPriceHasDiscountB());
        values.put(EUGameTable.Cols.GIFT_FINDER_DESCRIPTION_S, euGame.getGiftFinderDescriptionS());
        values.put(EUGameTable.Cols.TITLE, euGame.getTitle());
        values.put(EUGameTable.Cols.SORTING_TITLE, euGame.getSortingTitle());
        values.put(EUGameTable.Cols.COPYRIGHT_S, euGame.getCopyrightS());
        values.put(EUGameTable.Cols.GIFT_FINDER_CAROUSEL_IMAGE_URL_S, euGame.getGiftFinderCarouselImageUrlS());
        values.put(EUGameTable.Cols.PLAYERS_TO, euGame.getPlayersTo());
        values.put(EUGameTable.Cols.GIFT_FINDER_WISHLIST_IMAGE_URL_S, euGame.getGiftFinderWishListImageUrlS());
        values.put(EUGameTable.Cols.PRETTY_AGERATING_S, euGame.getPrettyAgeRatingS());
        values.put(EUGameTable.Cols.PLAYERS_FROM, euGame.getPlayersFrom());
        values.put(EUGameTable.Cols.AGE_RATING_TYPE, euGame.getAgeRatingType());
        values.put(EUGameTable.Cols.GIFT_FINDER_DETAIL_PAGE_STORE_LINK_S, euGame.getGiftFinderDetailPageStoreLinkS());
        values.put(EUGameTable.Cols.PRICE_SORTING_F, euGame.getPriceSortingF());
        values.put(EUGameTable.Cols.PRICE_LOWEST_F, euGame.getPriceLowestF());
        values.put(EUGameTable.Cols.AGE_RATING_VALUE, euGame.getAgeRatingValue());
        values.put(EUGameTable.Cols.PHYSICAL_VERSION_B, euGame.getPhysicalVersionB());
        values.put(EUGameTable.Cols.GAME_CATEGORIES_TXT, euGame.getGameCategoriesTxt());
        values.put(EUGameTable.Cols.PLAYABLE_ON_TXT, euGame.getPlayableOnTxt());
        values.put(EUGameTable.Cols.PRODUCT_CODE_TXT, euGame.getProductCodeTxt());
        values.put(EUGameTable.Cols.LANGUAGE_AVAILABILITY, euGame.getLanguageAvailability());
        values.put(EUGameTable.Cols.SYSTEM_TYPE, euGame.getSystemType());
        values.put(EUGameTable.Cols.DATES_RELEASED_DTS, euGame.getDatesReleasedDts());
        values.put(EUGameTable.Cols.PRETTY_GAME_CATEGORIES_TXT, euGame.getPrettyGameCategoriesTxt());
        values.put(EUGameTable.Cols.TITLE_EXTRAS_TXT, euGame.getTitleExtrasTxt());
        values.put(EUGameTable.Cols.NSUID_TXT, euGame.getNsUidTxt());
        values.put(EUGameTable.Cols.GAME_CATEGORY, euGame.getGameCategory());
        values.put(EUGameTable.Cols.SYSTEM_NAMES_TXT, euGame.getSystemNamesTxt());

        return values;
    }

    private EUGameCursorWrapper queryEUGames(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                EUGameTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new EUGameCursorWrapper(cursor);
    }

    /**
     * Parse Game Code.
     * @param gameCode A complete game code, but we need just 4-digit ( For linking USGame, EUGame and JPGame table )
     * @return Parsed 4-digit game code
     */
    private String parseGameCode(String gameCode) {
        return gameCode.length() == 9? gameCode.substring(4, 8): null;
    }
}
