package GameDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

public class EUGameCursorWrapper extends CursorWrapper {

    public EUGameCursorWrapper(Cursor cursor) {
        super(cursor);
    }
}
