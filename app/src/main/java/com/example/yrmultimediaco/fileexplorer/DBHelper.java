package com.example.yrmultimediaco.fileexplorer;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.mikepenz.fastadapter.IIdentifyable;
import com.mikepenz.fastadapter.IItem;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "searchData";
    public static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "searchHistory";
    public static final String _ID = "id";
    public static final String SEARCH = "search";

    public static final String BOOKMARK_TABLE_NAME = "bookmarks";
    public static final String ITEM_ID = "item_id";
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_IMAGE = "item_image";
    public static final String ITEM_DATE = "item_date";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ( "
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SEARCH + " TEXT )";

        String createSelectedItemQuery = "CREATE TABLE " + BOOKMARK_TABLE_NAME + " ( "
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_NAME + " TEXT, "
                + ITEM_IMAGE + " BLOB, "
                + ITEM_DATE + " TEXT ) ";

        db.execSQL(createSelectedItemQuery);
        db.execSQL(createTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL( "DROP TABLE IF EXISTS " + BOOKMARK_TABLE_NAME );
        onCreate(db);

    }

    public void addSelectedItems(String itemName, byte[] itemImage, String itemDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(ITEM_NAME, itemName);
            values.put(ITEM_IMAGE, itemImage);
            values.put(ITEM_DATE, itemDate);

            db.beginTransaction();
            db.insert(BOOKMARK_TABLE_NAME, null, values);
            db.setTransactionSuccessful(); // Mark the transaction as successful
        } catch (Exception e) {
            // Handle exceptions here
            db.close();
        } finally {
            db.endTransaction(); // End the transaction (commit if setTransactionSuccessful was called)

        }
    }

    public List<BookmarkAdapter> getAllBookmarkItems() {
        List<BookmarkAdapter> bookmarkItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.query(BOOKMARK_TABLE_NAME,
                    new String[]{ITEM_NAME, ITEM_IMAGE, ITEM_DATE},
                    null,
                    null,
                    null,
                    null,
                    null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String itemName = cursor.getString(0);
                    byte[] itemImage = cursor.getBlob(1);
                    String itemDate = cursor.getString(2);

                    BookmarkAdapter bookmarkItem = new BookmarkAdapter(itemName, itemImage, itemDate);
                    bookmarkItemList.add(bookmarkItem);

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            db.close();
        }

        return bookmarkItemList;
    }


    public void addSearchHistory(String search) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            // Check if the same query already exists in the search history
            if (!isQueryInHistory(search, db)) {
                values = new ContentValues();
                values.put(SEARCH, search);

            }

            db.insert(TABLE_NAME, null, values);

        } catch (Exception e) {
            db.close();
        }
    }

    public List<String> getAllSearchHistory(){
        List<String> searchHistoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try{
            Cursor cursor = db.query(TABLE_NAME, new String[]{SEARCH},null,null,null,null,null);

            if (cursor != null && cursor.moveToFirst()){
                do {
                    String searchQuery = cursor.getString(0);
                    searchHistoryList.add(searchQuery);

                } while (cursor.moveToNext());
                    cursor.close();

            }
        } catch (Exception e){
            db.close();
        }
        return searchHistoryList;
    }

    public void clearAllSearchHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null, null);
        } catch (Exception e){
            db.close();
        }
    }

    private boolean isQueryInHistory(String query, SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, new String[]{SEARCH}, SEARCH + " = ?", new String[]{query}, null, null, null);
        boolean queryExists = cursor.moveToFirst();
        cursor.close();
        return queryExists;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }



}
