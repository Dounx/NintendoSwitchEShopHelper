package GameGrabber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

import GameDbSchema.GameBaseHelper;
import GameDbSchema.JPGameCursorWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static GameDbSchema.GameDbSchema.*;

/**
 * Request all the games and the respond is a xml file
 * Japan's API can only return little info
 * So we just get nsuid from them
 */

public class JPGameGrabTask extends AsyncTask<String, Integer, Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED = 1;

    private final DownloadListener mListener;
    private final Context mContext;
    private final SQLiteDatabase mDatabase;

    public JPGameGrabTask(Context context, DownloadListener listener) {
        this.mListener = listener;
        this.mContext = context;
        this.mDatabase = new GameBaseHelper(mContext).getWritableDatabase();
    }

    @Override
    protected Integer doInBackground(String... params) {
        OkHttpClient client = new OkHttpClient();

        //// We can grab all the games info with a request
        Request request = new Request.Builder()
                .url("https://www.nintendo.co.jp/data/software/xml/switch.xml")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String responseData = response.body().string();
            parseXMLWithPullAndAddToDb(responseData);
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

    private void parseXMLWithPullAndAddToDb(String xmlData) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();

            JPGame jpGame = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        switch (nodeName) {
                            case "InitialCode":
                                jpGame = new JPGame();
                                jpGame.setInitialCode(parseGameCode(xmlPullParser.nextText()));
                                break;
                            case "TitleName":
                                jpGame.setTitleName(xmlPullParser.nextText());
                                break;
                            case "MakerName":
                                jpGame.setMakerName(xmlPullParser.nextText());
                                break;
                            case "MakerKana":
                                jpGame.setMakerKana(xmlPullParser.nextText());
                                break;
                            case "Price":
                                jpGame.setPrice(xmlPullParser.nextText());
                                break;
                            case "SalesDate":
                                jpGame.setSalesDate(xmlPullParser.nextText());
                                break;
                            case "SoftType":
                                jpGame.setSoftType(xmlPullParser.nextText());
                                break;
                            case "PlatformID":
                                jpGame.setPlatformID(xmlPullParser.nextText());
                                break;
                            case "DlIconFlg":
                                jpGame.setDlIconFlg(xmlPullParser.nextText());
                                break;
                            case "LinkURL":
                                // Parse the LinkURL to complete url
                                jpGame.setLinkURL("https://ec.nintendo.com/JP/ja" + xmlPullParser.nextText());
                                break;
                            case "ScreenshotImgFlg":
                                jpGame.setScreenshotImgFlg(xmlPullParser.nextText());
                                break;
                            case "ScreenshotImgURL":
                                jpGame.setScreenshotImgURL(xmlPullParser.nextText());
                                jpGame.setNsUid(parseNsUid(jpGame.getLinkURL()));
                                addJPGame(jpGame);
                                break;
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addJPGame(JPGame jpGame) {
        ContentValues values = getContentValues(jpGame);

        JPGameCursorWrapper cursor = queryJPGames("title_name = ?", new String[]{jpGame.getTitleName()});

        // If exist, just update info, else insert to it
        if (cursor.moveToFirst()) {
            mDatabase.update(JPGameTable.NAME, values, "title_name = ?", new String[]{jpGame.getTitleName()});
        } else{
            mDatabase.insert(JPGameTable.NAME, null, values);
        }
        cursor.close();
    }

    private static ContentValues getContentValues(JPGame jpGame) {
        ContentValues values = new ContentValues();
        values.put(JPGameTable.Cols.INITIAL_CODE, jpGame.getInitialCode());
        values.put(JPGameTable.Cols.NSUID, jpGame.getNsUid());
        values.put(JPGameTable.Cols.TITLE_NAME, jpGame.getTitleName());
        values.put(JPGameTable.Cols.MAKER_NAME, jpGame.getMakerName());
        values.put(JPGameTable.Cols.MAKER_KANA, jpGame.getMakerKana());
        values.put(JPGameTable.Cols.PRICE, jpGame.getPrice());
        values.put(JPGameTable.Cols.SALES_DATE, jpGame.getSalesDate());
        values.put(JPGameTable.Cols.SOFT_TYPE, jpGame.getSoftType());
        values.put(JPGameTable.Cols.PLATFORM_ID, jpGame.getPlatformID());
        values.put(JPGameTable.Cols.DL_ICON_FLG, jpGame.getDlIconFlg());
        values.put(JPGameTable.Cols.LINK_URL, jpGame.getLinkURL());
        values.put(JPGameTable.Cols.SCREENSHOT_IMG_FLG, jpGame.getScreenshotImgFlg());
        values.put(JPGameTable.Cols.SCREENSHOT_IMG_URL, jpGame.getScreenshotImgURL());

        return values;
    }

    private JPGameCursorWrapper queryJPGames(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                JPGameTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new JPGameCursorWrapper(cursor);
    }

    /**
     * Parse Game Code.
     * @param gameCode A complete game code, but we need just 4-digit ( For linking USGame, EUGame and JPGame table )
     * @return Parsed 4-digit game code
     */
    private String parseGameCode(String gameCode) {
        return gameCode.length() == 8? gameCode.substring(3, 7): null;
    }

    /**
     * Parse nsuid
     * @param linkUrl Contains nsuid
     * @return Nsuid
     */
    private String parseNsUid(String linkUrl) {
        return linkUrl.split("https://ec.nintendo.com/JP/ja/titles/")[1];
    }
}