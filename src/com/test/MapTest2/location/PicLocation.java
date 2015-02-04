package com.test.MapTest2.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Valerie on 22.07.2014.
 */
public class PicLocation
{
    final String TAG = "picLocation";


    public void saveLocation(double longitude, double latitude, String message, Context context)
    {
        DBHelper dbHelper = new DBHelper(context);
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.d(TAG, "--- Insert in coordinates: ---");
        cv.put("longitude", longitude);
        cv.put("latitude", latitude);
        cv.put("message", message);
        long rowID = db.insert("coordinates", null, cv);
        Log.d(TAG, "row inserted, ID = " + rowID);

        Log.d(TAG, "--- Rows in coordinates: ---");
        Cursor c = db.query("coordinates", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int longitudeColIndex = c.getColumnIndex("longitude");
            int latitudeColIndex = c.getColumnIndex("latitude");
            int messageColIndex = c.getColumnIndex("message");
            do {
                Log.d(TAG,
                        "ID = " + c.getInt(idColIndex) +
                        ", longitude = " + c.getString(longitudeColIndex) +
                        ", latitude = " + c.getString(latitudeColIndex) +
                        ", message = " + c.getString(messageColIndex));
            } while (c.moveToNext());
        } else
            Log.d(TAG, "0 rows");
        c.close();
        dbHelper.close();

    }

    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "coordinates", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table coordinates ("
                       + "id integer primary key autoincrement,"
                       + "longitude double,"
                       + "latitude double,"
                       + "message text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
