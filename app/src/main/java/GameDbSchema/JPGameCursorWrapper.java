package GameDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

public class JPGameCursorWrapper extends CursorWrapper {
    public JPGameCursorWrapper(Cursor cursor) {
        super(cursor);
    }
}
