package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelperLocal extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "cities_database";
    private static final String TABLE_NAME = "selected_cities";
    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CITY = "city";
    private static final String COLUMN_URL_PIC = "url_pic";
    private static final String COLUMN_DESS = "dess";
    private static final String COLUMN_DESS_AI = "dessAi";
    private static final String COLUMN_SSILKA = "ssilka";
    private static final String COLUMN_UID = "uid";
    private static final String COL_CITY = "city";

    public DatabaseHelperLocal(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT NOT NULL,"
            + COLUMN_CITY + " TEXT NOT NULL,"
            + COLUMN_URL_PIC + " TEXT NOT NULL,"
            + COLUMN_DESS + " TEXT NOT NULL,"
            + COLUMN_DESS_AI + " TEXT,"
            + COLUMN_SSILKA + " TEXT NOT NULL,"
            + COLUMN_UID + " TEXT NOT NULL"
            + ")";

    private static final String createTable =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CITY + " TEXT UNIQUE)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CITY + " TEXT UNIQUE)";
        db.execSQL(createTable);
        db.execSQL(CREATE_TABLE_HISTORY);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String city) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_CITY, city);

            // Проверяем, есть ли уже записи в таблице
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
            int rowCount = 0;
            if (cursor.moveToFirst()) {
                rowCount = cursor.getInt(0);
            }
            cursor.close();

            if (rowCount == 0) {
                // Если таблица пуста, добавляем новую запись
                db.insertOrThrow(TABLE_NAME, null, contentValues);
            } else {
                // Если таблица не пуста, обновляем первую строку
                db.update(TABLE_NAME, contentValues, COLUMN_ID + "=1", null);
            }

            return true;
        } catch (Exception e) {
            Log.e("DatabaseHelperLocal", "Ошибка при добавлении данных: ", e);
            return false;
        }
    }



    public String getCity() {
        String city = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CITY + " FROM " + TABLE_NAME, null);
        try {
            if (cursor.moveToFirst()) {
                int cityColumnIndex = cursor.getColumnIndex(COLUMN_CITY);
                if (cityColumnIndex != -1) {
                    city = cursor.getString(cityColumnIndex);
                    Log.e("DatabaseHelperLocal", "Ошибка при добавлении данных: мы берём " + city);
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelperLocal", "Ошибка при получении значения city: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return city;
    }


    public long insertHistory(String name_TABLE, String name, String city, String urlPic, String dess, String dessAi, String ssilka, String uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_URL_PIC, urlPic);
        values.put(COLUMN_DESS, dess);
        values.put(COLUMN_DESS_AI, dessAi);
        values.put(COLUMN_SSILKA, ssilka);
        values.put(COLUMN_UID, uid);
        return db.insert(name_TABLE, null, values);
    }
    public Cursor getAllHistoryData(String name_TABLE) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_URL_PIC, COLUMN_NAME, COLUMN_UID};
        return db.query(name_TABLE, projection, null, null, null, null, null);
    }
    public void closeDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
    public void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }





}
