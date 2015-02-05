package com.next.eswaraj.helpers;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.next.eswaraj.models.ComplaintRequestDBItem;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "complaintDB.db";
    public static final String TABLE_COMPLAINTS = "complaints";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_COMPLAINT = "complaint";
    public static final String COLUMN_FILE = "file";
    public static final String COLUMN_VALID = "valid";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COMPLAINTS_TABLE = "CREATE TABLE " + TABLE_COMPLAINTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_COMPLAINT + " TEXT," + COLUMN_FILE + " TEXT," + COLUMN_VALID + " INTEGER" + ")";
        db.execSQL(CREATE_COMPLAINTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE " + TABLE_COMPLAINTS;
        db.execSQL(query);
        String CREATE_COMPLAINTS_TABLE = "CREATE TABLE " + TABLE_COMPLAINTS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_COMPLAINT + " TEXT," + COLUMN_FILE + " TEXT," + COLUMN_VALID + " INTEGER" + ")";
        db.execSQL(CREATE_COMPLAINTS_TABLE);
    }

    public void addComplaint(ComplaintRequestDBItem complaintRequestDBItem) {

        Log.d("DatabaseHelper", "Writing to database: " + complaintRequestDBItem.toString());
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLAINT, complaintRequestDBItem.getRequest());
        values.put(COLUMN_FILE, complaintRequestDBItem.getFile());
        values.put(COLUMN_VALID, 1);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_COMPLAINTS, null, values);
        db.close();
    }

    public ComplaintRequestDBItem getOneComplaint() {
        String query = "Select * FROM " + TABLE_COMPLAINTS + " WHERE " + COLUMN_VALID + " = 1" + " LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ComplaintRequestDBItem complaintRequestDBItem = new ComplaintRequestDBItem();

        if (cursor.moveToFirst()) {
            complaintRequestDBItem.setId(Integer.parseInt(cursor.getString(0)));
            complaintRequestDBItem.setRequest(cursor.getString(1));
            complaintRequestDBItem.setFile(cursor.getString(2));
            Log.d("DatabaseHelper", "Reading from database: " + complaintRequestDBItem.toString());
            cursor.close();
        } else {
            complaintRequestDBItem = null;
            Log.d("DatabaseHelper", "Reading from database: Null");
        }
        db.close();
        return complaintRequestDBItem;
    }

    public int deleteComplaint(ComplaintRequestDBItem complaintRequestDBItem) {
        int result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.delete(TABLE_COMPLAINTS, COLUMN_ID + " = ?", new String[] { String.valueOf(complaintRequestDBItem.getId()) });
        db.close();
        return result;
    }

    public void markInvalid(ComplaintRequestDBItem complaintRequestDBItem) {
        String query = "UPDATE " + TABLE_COMPLAINTS + " SET " + COLUMN_VALID + " = 0 WHERE " + COLUMN_ID + " = " + complaintRequestDBItem.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    public void markAllValid() {
        String query = "UPDATE " + TABLE_COMPLAINTS + " SET " + COLUMN_VALID + " = 1";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }
}
