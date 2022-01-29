package com.zemmyang.gyroscopetrackerdemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;

public class DBManager {
    private DBHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(long timestamp, float[] gyroValues) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DBHelper.TIMESTAMP, timestamp);
        contentValue.put(DBHelper.ROTATION_X, gyroValues[0]);
        contentValue.put(DBHelper.ROTATION_Y, gyroValues[1]);
        contentValue.put(DBHelper.ROTATION_Z, gyroValues[2]);
        database.insert(DBHelper.TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[] { DBHelper.ID, DBHelper.TIMESTAMP, DBHelper.ROTATION_X, DBHelper.ROTATION_Y, DBHelper.ROTATION_Z };
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public int update(long _id, long timestamp, float[] gyroValues) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.TIMESTAMP, timestamp);
        contentValues.put(DBHelper.ROTATION_X, gyroValues[0]);
        contentValues.put(DBHelper.ROTATION_Y, gyroValues[1]);
        contentValues.put(DBHelper.ROTATION_Z, gyroValues[2]);
        return database.update(DBHelper.TABLE_NAME, contentValues, DBHelper.ID + " = " + _id, null);
    }

    public void delete(long _id) {
        database.delete(DBHelper.TABLE_NAME, DBHelper.ID + "=" + _id, null);
    }

    public void deleteAll(){
        dbHelper.dropTable(database);
    }

    public String display(int lines){
        PrettifyRecord pr = new PrettifyRecord();
        StringBuilder sb = new StringBuilder();
        Cursor c = fetch();

        int count = c.getCount();
        int startDisplay = (count >= lines) ? count - lines : 1;

        for (int i = startDisplay; i < count; i++) {
            float[] values = {c.getFloat(2), c.getFloat(3), c.getFloat(4)};
            String stringOut = pr.getDisplay(c.getLong(1), values);
            sb.append(stringOut).append("\n");
            c.moveToNext();
        }
        return sb.toString();

    }

    public void saveAsCSVToCache(String filename){
        try
        {
            Cursor c = fetch();

            File outputDir = context.getExternalCacheDir();
            File csvLocation = new File(outputDir, filename + ".csv");

            FileWriter writer = new FileWriter(csvLocation);

            String header = String.join(",", c.getColumnNames());
            writer.append(header);

            int count = c.getCount();

            for (int i = 1; i < count; i++) {
                String stringOut = c.getString(1) + ","  + c.getFloat(2) + "," + c.getFloat(3) + "," + c.getFloat(4) + "\n";
                writer.append(stringOut);
                c.moveToNext();
            }

            writer.flush();
            writer.close();

        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }

    }

}
