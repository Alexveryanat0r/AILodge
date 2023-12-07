package com.example.myapplication;

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
        // No implementation needed as the database already exists
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
        String[] columns = {"hotel_name", "city", "link_reservation", "description"};
        String selection = "hotel_name=? AND city=?";
        String[] selectionArgs = {hotel, city};

        Cursor cursor = db.query("info", columns, selection, selectionArgs, null, null, null);

        StringBuilder result = new StringBuilder();

        if (cursor.moveToFirst()) {
            int hotelIndex = cursor.getColumnIndex("hotel_name");
            int cityIndex = cursor.getColumnIndex("city");
            int linkIndex = cursor.getColumnIndex("link_reservation");
            int descIndex = cursor.getColumnIndex("description");

            do {
                String hotelName = cursor.getString(hotelIndex);
                String cityName = cursor.getString(cityIndex);
                String linkReservation = cursor.getString(linkIndex);
                String description = cursor.getString(descIndex);

                result.append("Hotel: ").append(hotelName).append("\n");
                result.append("City: ").append(cityName).append("\n");
                result.append("Reservation Link: ").append(linkReservation).append("\n");
                result.append("Description: ").append(description).append("\n\n");
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



    public String getCombinedInfoForHotel(String hotelName, String city) {
        int hotelId = getIdByHotelNameAndCity(hotelName, city);

        // Example 1: Get hotel information

        String combinedInfo = getHotelInfoById(hotelId) +

                // Example 2: Get all convenience values
                getAllConvenienceValuesForHotel(hotelId) +
                "коментарии: " +
                // Example 3: Get all thesis values
                getAllThesisForHotel(hotelId);

        return combinedInfo;
    }

}