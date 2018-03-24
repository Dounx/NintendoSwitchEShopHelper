package GameGrabber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import GameDbSchema.GameBaseHelper;
import GameDbSchema.GameDbSchema.SupportedCountryTable;
import GameDbSchema.SupportedCountryCursorWrapper;

/**
 * Singleton pattern for SupportedCountry class
 */
public class SupportedCountryLab {
    private static  SupportedCountryLab sSupportedCountryLab;
    private final Context mContext;
    private final SQLiteDatabase mDatabase;
    public List<SupportedCountry> mCountries;

    public static SupportedCountryLab get(Context context) {
        if (sSupportedCountryLab == null) {
            sSupportedCountryLab = new SupportedCountryLab(context);
        }
        return sSupportedCountryLab;
    }

    private SupportedCountryLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new GameBaseHelper(mContext).getWritableDatabase();
    }

    public SupportedCountry getSupportedCountry(String code) {
        List<SupportedCountry> list = getSupportedCountries();

        for (SupportedCountry supportedCountry : list) {
            if (supportedCountry.getCode().equals(code)) {
                return supportedCountry;
            }
        }

        return null;
    }

    public SupportedCountry getCountry(String code) {
        List<SupportedCountry> list = mCountries;
        for (SupportedCountry supportedCountry : list) {
            if (supportedCountry.getCode().equals(code)) {
                return supportedCountry;
            }
        }
        return null;
    }

    public List<SupportedCountry> getSupportedCountries() {
        List<SupportedCountry> supportedCountries = new ArrayList<>();

        try (SupportedCountryCursorWrapper cursor = querySupportedCountries(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                supportedCountries.add(cursor.getSupportedCountry());
                cursor.moveToNext();
            }
        }
        return supportedCountries;
    }

    public void addSupportedCountry(SupportedCountry supportedCountry) {
        ContentValues values = getContentValues(supportedCountry);

        try (SupportedCountryCursorWrapper cursor = querySupportedCountries("name = ?", new String[]{supportedCountry.getName()})) {

            // If exist, just update info, else insert to it
            if (cursor.moveToFirst()) {
                mDatabase.update(SupportedCountryTable.NAME, values, "name = ?", new String[]{supportedCountry.getName()});
            } else{
                mDatabase.insert(SupportedCountryTable.NAME, null, values);
            }
        }
    }

    private static ContentValues getContentValues(SupportedCountry supportedCountry) {

        ContentValues values = new ContentValues();
        values.put(SupportedCountryTable.Cols.NAME, supportedCountry.getName());
        values.put(SupportedCountryTable.Cols.CODE, supportedCountry.getCode());
        values.put(SupportedCountryTable.Cols.CURRENCY, supportedCountry.getCurrency());
        values.put(SupportedCountryTable.Cols.BELONG, supportedCountry.getBelong());

        return values;
    }

    private SupportedCountryCursorWrapper querySupportedCountries(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                SupportedCountryTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new SupportedCountryCursorWrapper(cursor);
    }
}
