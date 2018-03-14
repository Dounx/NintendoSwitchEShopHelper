package GameDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import GameGrabber.SupportedCountry;

import static GameDbSchema.GameDbSchema.*;

public class SupportedCountryCursorWrapper extends CursorWrapper {
    public SupportedCountryCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public SupportedCountry getSupportedCountry() {
        String name = getString(getColumnIndex(SupportedCountryTable.Cols.NAME));
        String code = getString(getColumnIndex(SupportedCountryTable.Cols.CODE));
        String currency = getString(getColumnIndex(SupportedCountryTable.Cols.CURRENCY));
        String belong = getString(getColumnIndex(SupportedCountryTable.Cols.BELONG));

        SupportedCountry supportedCountry = new SupportedCountry();
        supportedCountry.setName(name);
        supportedCountry.setCode(code);
        supportedCountry.setCurrency(currency);
        supportedCountry.setBelong(belong);

        return supportedCountry;
    }
}
