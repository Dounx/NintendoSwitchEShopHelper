package GameDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

public class USGameCursorWrapper extends CursorWrapper {
    public USGameCursorWrapper(Cursor cursor) {
        super(cursor);
    }
}
