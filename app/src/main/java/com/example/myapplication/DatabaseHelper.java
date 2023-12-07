package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class DatabaseHelper extends SQLiteOpenHelper {

    private DatabaseReference databaseReference;
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "project.db";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }



    public void createDatabase(Context context) throws IOException {
        if (!checkDatabaseExists()) {
            copyDatabase(context);
        }
    }

    private boolean checkDatabaseExists() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    private void copyDatabase(Context context) throws IOException {
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();

        File file = new File(outFileName);
        file.getParentFile().mkdirs(); // Создаем директории, если их нет

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public SQLiteDatabase openDatabase(Context context) throws SQLException {
        return this.getWritableDatabase();
    }


    @Override
    public synchronized void close() {
        // Закрываем базу данных при необходимости
        super.close();
    }

    // Добавленные методы
    public String getAvatarPathByUid(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT avatar FROM users WHERE uid=?", new String[]{uid});

        String avatarPath = null;
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex("avatar");
            if (columnIndex != -1) {
                avatarPath = cursor.getString(columnIndex);
            }
        }

        cursor.close();
        db.close();
        return avatarPath;
    }
    public String getUsernameByUid(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String username = null;

        try {
            String query = "SELECT username FROM users WHERE uid = ?";
            cursor = db.rawQuery(query, new String[]{uid});

            if (cursor != null && cursor.moveToFirst()) {
                int usernameIndex = cursor.getColumnIndex("username");
                if (usernameIndex >= 0) {
                    username = cursor.getString(usernameIndex);
                }
            } else {
                Log.e(TAG, "Cursor is null or empty");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        Log.d(TAG, "getUsernameByUid: uid=" + uid + ", username=" + username);
        return username;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Обновление базы данных (при необходимости)
    }

    public void addUsernameColumnToChatsTable(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем, существует ли таблица "chats"
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='chats'", null);
        boolean tableExists = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }

        if (!tableExists) {
            // Если таблицы "chats" нет, создаем ее
            String createTableQuery = "CREATE TABLE IF NOT EXISTS chats (id INTEGER PRIMARY KEY AUTOINCREMENT, " + username + " TEXT);";
            db.execSQL(createTableQuery);
        } else {
            // Если таблица "chats" уже существует, проверяем наличие столбца "username"
            cursor = db.rawQuery("PRAGMA table_info(chats)", null);
            boolean columnExists = false;

            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex("name");
                while (cursor.moveToNext()) {
                    if (nameIndex != -1) {
                        String columnName = cursor.getString(nameIndex);
                        if (username.equals(columnName)) {
                            columnExists = true;
                            break;
                        }
                    }
                }

                cursor.close();
            }

            // Если столбца "username" нет, добавляем его
            if (!columnExists) {
                String alterTableQuery = "ALTER TABLE chats ADD COLUMN " + username + " TEXT;";
                db.execSQL(alterTableQuery);
            }
        }

        db.close();
    }
    public boolean userExistsInChats(String username, String userValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Проверяем, существует ли столбец в таблице
            String checkColumnQuery = "PRAGMA table_info(chats)";
            Cursor columnCursor = db.rawQuery(checkColumnQuery, null);

            if (columnCursor != null) {
                int nameIndex = columnCursor.getColumnIndex(username);

                if (nameIndex != -1) {
                    // Выполняем параметризированный запрос на проверку наличия записи
                    String query = "SELECT EXISTS (SELECT 1 FROM chats WHERE " + username + " = ?)";
                    Cursor cursor = db.rawQuery(query, new String[]{userValue});

                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            int exists = cursor.getInt(0);
                            return exists == 1;
                        }

                        cursor.close();
                    }
                } else {
                    // Обработка ситуации, когда столбец не найден
                    Log.e("Error", "Column 'name' not found in the table");
                }
                columnCursor.close();
            }
            return false;
        } finally {
            db.close();
        }
    }
    public void addUserToChats(String columnUsername, String myusername) {
        if (columnUsername == null || columnUsername.isEmpty()) {
            Log.e("Error", "Column username is null or empty");
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(columnUsername, myusername);  // Use the variable columnUsername as the column name
            db.insert("chats", null, values);
            Log.e("Successful", "Mi dobavili");
        } finally {
            db.close();
        }
    }

    public boolean isUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Выполняем запрос
            cursor = db.query("users", null, "username=?", new String[]{username}, null, null, null);

            // Проверяем, есть ли данные
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return false; // В случае ошибки
    }

    public boolean saveUserToLocalDatabase(String username, String uid, Context context) {
        try {
            SQLiteDatabase db = openDatabase(context);

            ContentValues values = new ContentValues();
            values.put("username", username);
            values.put("uid", uid);
            db.insert("users", null, values);
            db.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

