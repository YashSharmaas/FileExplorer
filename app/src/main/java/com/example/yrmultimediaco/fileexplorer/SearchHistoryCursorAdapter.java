package com.example.yrmultimediaco.fileexplorer;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

public class SearchHistoryCursorAdapter extends SimpleCursorAdapter {

    public SearchHistoryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(DBHelper.SEARCH);
        return cursor.getString(columnIndex);
    }

}
