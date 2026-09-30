package com.online.flyer.design.postermaker.Leaflet_utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class Leaflet_NotificationDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "notifications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_IS_READ = "is_read";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LAUNCH_URL = "launch_url";

    public Leaflet_NotificationDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_TIMESTAMP + " INTEGER, "
                + COLUMN_IS_READ + " INTEGER, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_LAUNCH_URL + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addNotification(String title, String message, String image, String launchUrl) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prevent duplicates
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE + "=? AND " + COLUMN_MESSAGE + "=?", new String[]{title, message});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return;
        }
        if (cursor != null) cursor.close();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
        values.put(COLUMN_IS_READ, 0); // 0 = unread
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_LAUNCH_URL, launchUrl);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int getUnreadCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + COLUMN_IS_READ + " = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public void markAsRead(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void markAllAsRead() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        db.update(TABLE_NAME, values, null, null);
        db.close();
    }
    
    public void deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public List<Leaflet_NotificationModel> getAllNotifications() {
        List<Leaflet_NotificationModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIMESTAMP + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Leaflet_NotificationModel model = new Leaflet_NotificationModel();
                model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                model.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
                model.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                model.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);
                model.setImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)));
                model.setLaunchUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAUNCH_URL)));
                list.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}

