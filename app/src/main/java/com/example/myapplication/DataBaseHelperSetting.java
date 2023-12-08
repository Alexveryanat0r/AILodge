package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelperSetting extends SQLiteOpenHelper {

    private static final String DB_NAME = "mtst_last.db";
    private static final String DB_PATH = "/data/data/com.example.myapplication/databases/";
    private static final String COLUMN_HOTEL_NAME = "hotel_name";
    private static final String COL_CITY = "city";

    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_EMOTIONAL_TONE = "emotional_tone";
    private static final String COLUMN_TARGET_AUDIENCE = "target_audience";
    private static final String COLUMN_SEASON_DESCRIPTION = "season_description";
    private static final String COLUMN_FORMALITY_LEVEL = "formality_level";
    private static final String COLUMN_MAIN_COLOR_ACCENT = "main_color_accent";
    private static final String COLUMN_HIGHLIGHT_FEATURES = "highlight_features";
    private static final String COLUMN_NEW_EMOTIONAL_TONE = "new_emotional_tone";
    private static final String COLUMN_PROMPT_ENEBLE = "prompt_eneble";
    private static final String COLUMN_PROMPT_UNENEBLE = "prompt_uneneble";
    private static final String COLUMN_UID = "uid";

    // SQL-запрос для создания таблицы
    private static final String CREATE_TABLE_SETTINGS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SETTINGS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_UID + "TEXT, " +
                    COLUMN_EMOTIONAL_TONE + " TEXT, " +
                    COLUMN_TARGET_AUDIENCE + " TEXT, " +
                    COLUMN_SEASON_DESCRIPTION + " TEXT, " +
                    COLUMN_FORMALITY_LEVEL + " TEXT, " +
                    COLUMN_MAIN_COLOR_ACCENT + " TEXT, " +
                    COLUMN_HIGHLIGHT_FEATURES + " TEXT, " +
                    COLUMN_NEW_EMOTIONAL_TONE + " TEXT, " +
                    COLUMN_PROMPT_ENEBLE + " TEXT, " +
                    COLUMN_PROMPT_UNENEBLE + " TEXT);";

    public DataBaseHelperSetting(Context context) {
        super(context, DB_NAME, null, 1);

        copyDataBase(context);
    }

    private void copyDataBase(Context context) {
        String outFileName = DB_PATH + DB_NAME;

        // Check if the database already exists in the target location
        if (checkDataBaseExists(context)) {
            return;
        }

        try (InputStream mInput = context.getAssets().open(DB_NAME);
             OutputStream mOutput = new FileOutputStream(outFileName)) {

            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0) {
                mOutput.write(mBuffer, 0, mLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDataBaseExists(Context context) {
        SQLiteDatabase checkDB = null;

        try {
            String mPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // Database does not exist
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SETTINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Delete the old database
    }

    public List<String> getUniqueCities() {
        List<String> cities = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT DISTINCT city FROM info", null);
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex("city");
                if (columnIndex != -1 && cursor.moveToFirst()) {
                    do {
                        String city = cursor.getString(columnIndex);
                        cities.add(city);
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return cities;
    }
    public List<String> getHotelsInCity(String city) {
        List<String> hotels = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_HOTEL_NAME};
        String selection = COL_CITY + "=?";
        String[] selectionArgs = {city};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int hotelIndex = cursor.getColumnIndexOrThrow(COLUMN_HOTEL_NAME);
                if (hotelIndex != -1) {
                    String hotel = cursor.getString(hotelIndex);
                    if (hotel != null) {
                        hotels.add(hotel);
                    }
                }
            }
            cursor.close();
        }

        return hotels;
    }
    public boolean exists(String city, String hotel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_CITY};
        String selection = COL_CITY + "=? AND " + COLUMN_HOTEL_NAME + "=?";
        String[] selectionArgs = {city, hotel};
        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        boolean result = cursor.moveToFirst();
        cursor.close();
        return result;
    }
    public String getLinkReservation(String hotelName, String cityName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"link_reservation"};
        String selection = "hotel_name=? AND city=?";
        String[] selectionArgs = {hotelName, cityName};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        String linkReservation = null;

        if (cursor.moveToFirst()) {
            int linkReservationIndex = cursor.getColumnIndex("link_reservation");
            if (linkReservationIndex != -1) {
                linkReservation = cursor.getString(linkReservationIndex);
            }
        }

        cursor.close();
        return linkReservation;
    }
    public String getdess(String hotelName, String cityName){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"description"};
        String selection = "hotel_name=? AND city=?";
        String[] selectionArgs = {hotelName, cityName};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        String linkReservation = null;

        if (cursor.moveToFirst()) {
            int dess = cursor.getColumnIndex("description");
            if (dess != -1) {
                linkReservation = cursor.getString(dess);
            }
        }

        cursor.close();
        return linkReservation;
    }
    public String getHotelInfo(String city, String hotel) {
        SQLiteDatabase db = this.getReadableDatabase();
//        String[] columns = {"hotel_name", "city", "link_reservation", "description"};
        String[] columns = {"hotel_name", "city", "link_reservation"};
        String selection = "hotel_name=? AND city=?";
        String[] selectionArgs = {hotel, city};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int hotelIndex = cursor.getColumnIndex("hotel_name");
            int cityIndex = cursor.getColumnIndex("city");
            int linkIndex = cursor.getColumnIndex("link_reservation");
//            int descIndex = cursor.getColumnIndex("description");

            do {
                String hotelName = cursor.getString(hotelIndex);
                String cityName = cursor.getString(cityIndex);
                String linkReservation = cursor.getString(linkIndex);
//                String description = cursor.getString(descIndex);

                result.append("Hotel: ").append(hotelName).append("\n");
                result.append("City: ").append(cityName).append("\n");
                result.append("Reservation Link: ").append(linkReservation).append("\n");
//                result.append("Description: ").append(description).append("\n\n");
            } while (cursor.moveToNext());
        }

        cursor.close();
        return result.toString();
    }
    public String getAllConvenienceValuesForHotel(int hotelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"value"};
        String selection = "hotelID=?";
        String[] selectionArgs = {String.valueOf(hotelId)};

        Cursor cursor = db.query("convenience", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int valueIndex = cursor.getColumnIndex("value");

            do {
                String convenienceValue = cursor.getString(valueIndex);
                result.append(convenienceValue).append(", "); // Separate values with a comma or another delimiter
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Remove the trailing comma and space
        if (result.length() > 0) {
            result.delete(result.length() - 2, result.length());
        }

        return result.toString();
    }
    public String getAllThesisForHotel(int hotelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"thesis"};
        String selection = "hotelID=?";
        String[] selectionArgs = {String.valueOf(hotelId)};

        Cursor cursor = db.query("comments", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int thesisIndex = cursor.getColumnIndex("thesis");

            do {
                String thesisValue = cursor.getString(thesisIndex);
                result.append(thesisValue).append("\n"); // Separate values by a newline or another delimiter
            } while (cursor.moveToNext());
        }

        cursor.close();

        return result.toString();
    }
    public String getAllRateValuesForHotel(int hotelId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"rate_value"};
        String selection = "hotelID=?";
        String[] selectionArgs = {String.valueOf(hotelId)};

        Cursor cursor = db.query("rating", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int rateValueIndex = cursor.getColumnIndex("rate_value");

            do {
                int rateValue = cursor.getInt(rateValueIndex);
                result.append(rateValue).append(", "); // Separate values with a comma or another delimiter
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Remove the trailing comma and space
        if (result.length() > 0) {
            result.delete(result.length() - 2, result.length());
        }

        return result.toString();
    }
    public int getIdByHotelNameAndCity(String hotelName, String city) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"ID", "city"};
        String selection = "hotel_name=? AND city=?";
        String[] selectionArgs = {hotelName, city};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);
//
        StringBuilder result = new StringBuilder();
        int Id = -1;
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("ID");
            Id = cursor.getInt(idIndex);

        }
        return Id;
    }
    public String getHotelInfoById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "global_rate", "star", "description"};
        String selection = "ID=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int globalRateIndex = cursor.getColumnIndex("global_rate");
            int starIndex = cursor.getColumnIndex("star");
            int descriptionIndex = cursor.getColumnIndex("description");

            do {
                float globalRate = cursor.getFloat(globalRateIndex);
                int star = cursor.getInt(starIndex);
                String description = cursor.getString(descriptionIndex);

                result.append("Global Rate: ").append(globalRate).append("\n");
                result.append("Star: ").append(star).append("\n");
                result.append("Description: ").append(description).append("\n\n");
            } while (cursor.moveToNext());
        }

        cursor.close();

        return result.toString();
    }

    public String getHotelInfoByIdWithoutDess(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { "hotel_name, global_rate", "star"};
        String selection = "ID=?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int hotel_name = cursor.getColumnIndex("hotel_name");
            int globalRateIndex = cursor.getColumnIndex("global_rate");
            int starIndex = cursor.getColumnIndex("star");

            do {
                String name = cursor.getString(hotel_name);
                float globalRate = cursor.getFloat(globalRateIndex);
                int star = cursor.getInt(starIndex);

                result.append("Hotel name: ").append(name).append("\n");
                result.append("Global Rate: ").append(globalRate).append("\n");
                result.append("Star: ").append(star).append("\n\n");
            } while (cursor.moveToNext());
        }

        cursor.close();

        return result.toString();
    }


    public String getCombinedInfoForHotel(String hotelName, String city) {
        int hotelId = getIdByHotelNameAndCity(hotelName, city);

        StringBuilder combinedInfo = new StringBuilder();

        // Example 1: Get hotel information
        combinedInfo.append(getHotelInfoByIdWithoutDess(hotelId));


        // Example 2: Get all convenience values
        combinedInfo.append(getAllConvenienceValuesForHotel(hotelId));
        combinedInfo.append("коментарии: ");
        // Example 3: Get all thesis values
        combinedInfo.append(getAllThesisForHotel(hotelId));

        return combinedInfo.toString();
    }
    public long addSettingsWithPrompts(String uid, String emotionalTone, String targetAudience, String seasonDescription, String formalityLevel, String mainColorAccent, String highlightFeatures, String newEmotionalTone, String promptEneble, String promptUneneble) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Проверяем, существует ли запись с таким же ID
        String whereClause = COLUMN_UID + "=?";
        String[] whereArgs = {uid};
        Cursor cursor = db.query(TABLE_SETTINGS, null, whereClause, whereArgs, null, null, null);
        boolean recordExists = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, uid);
        values.put(COLUMN_EMOTIONAL_TONE, emotionalTone);
        values.put(COLUMN_TARGET_AUDIENCE, targetAudience);
        values.put(COLUMN_SEASON_DESCRIPTION, seasonDescription);
        values.put(COLUMN_FORMALITY_LEVEL, formalityLevel);
        values.put(COLUMN_MAIN_COLOR_ACCENT, mainColorAccent);
        values.put(COLUMN_HIGHLIGHT_FEATURES, highlightFeatures);
        values.put(COLUMN_NEW_EMOTIONAL_TONE, newEmotionalTone);
        values.put(COLUMN_PROMPT_ENEBLE, promptEneble);
        values.put(COLUMN_PROMPT_UNENEBLE, promptUneneble);

        if (recordExists) {
            long result = db.update(TABLE_SETTINGS, values, whereClause, whereArgs);
            db.close();
            return result;
        } else {
            long result = db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
            return result;
        }
    }



    public void getAllSettings() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // Извлекаем данные по столбцам
                    int emotionalToneIndex = cursor.getColumnIndex(COLUMN_EMOTIONAL_TONE);
                    String emotionalTone = (emotionalToneIndex != -1) ? cursor.getString(emotionalToneIndex) : "N/A";

                    int targetAudienceIndex = cursor.getColumnIndex(COLUMN_TARGET_AUDIENCE);
                    String targetAudience = (targetAudienceIndex != -1) ? cursor.getString(targetAudienceIndex) : "N/A";

                    int seasonDescriptionIndex = cursor.getColumnIndex(COLUMN_SEASON_DESCRIPTION);
                    String seasonDescription = (seasonDescriptionIndex != -1) ? cursor.getString(seasonDescriptionIndex) : "N/A";

                    int formalityLevelIndex = cursor.getColumnIndex(COLUMN_FORMALITY_LEVEL);
                    String formalityLevel = (formalityLevelIndex != -1) ? cursor.getString(formalityLevelIndex) : "N/A";

                    int mainColorAccentIndex = cursor.getColumnIndex(COLUMN_MAIN_COLOR_ACCENT);
                    String mainColorAccent = (mainColorAccentIndex != -1) ? cursor.getString(mainColorAccentIndex) : "N/A";

                    int highlightFeaturesIndex = cursor.getColumnIndex(COLUMN_HIGHLIGHT_FEATURES);
                    String highlightFeatures = (highlightFeaturesIndex != -1) ? cursor.getString(highlightFeaturesIndex) : "N/A";

                    int newEmotionalToneIndex = cursor.getColumnIndex(COLUMN_NEW_EMOTIONAL_TONE);
                    String newEmotionalTone = (newEmotionalToneIndex != -1) ? cursor.getString(newEmotionalToneIndex) : "N/A";

                    int promptEnebleIndex = cursor.getColumnIndex(COLUMN_PROMPT_ENEBLE);
                    String promptEneble = (promptEnebleIndex != -1) ? cursor.getString(promptEnebleIndex) : "N/A";

                    int promptUnenebleIndex = cursor.getColumnIndex(COLUMN_PROMPT_UNENEBLE);
                    String promptUneneble = (promptUnenebleIndex != -1) ? cursor.getString(promptUnenebleIndex) : "N/A";

                    // Выводим данные в консоль
                    System.out.println("Emotional Tone: " + emotionalTone);
                    System.out.println("Target Audience: " + targetAudience);
                    System.out.println("Season Description: " + seasonDescription);
                    System.out.println("Formality Level: " + formalityLevel);
                    System.out.println("Main Color Accent: " + mainColorAccent);
                    System.out.println("Highlight Features: " + highlightFeatures);
                    System.out.println("New Emotional Tone: " + newEmotionalTone);
                    System.out.println("Prompt Eneble: " + promptEneble);
                    System.out.println("Prompt Uneneble: " + promptUneneble);
                    System.out.println("---------------------");
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }
    }


}