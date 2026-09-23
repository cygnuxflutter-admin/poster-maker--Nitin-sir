package com.online.flyer.design.postermaker.Leaflet_utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.online.flyer.design.postermaker.Leaflet_MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Leaflet_SignatureFileUtils {

    private final Context context;
    private final List<String> FILE_EXTN = new ArrayList<>();

    public Leaflet_SignatureFileUtils(Context context) {
        this.context = context;
    }

    public static Uri getContentUriForImageFromMediaStore(Context context, String path) {
        ContentResolver resolver = context.getContentResolver();
        //Uri photoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // to handle hidden images
        Uri photoUri = MediaStore.Files.getContentUri("external");
        Cursor cursor = resolver.query(photoUri, new String[]{BaseColumns._ID}, MediaStore.MediaColumns.DATA + " = ?", new String[]{path}, null);
        if (cursor == null) {
            return Uri.parse(path);
        }
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            cursor.close();
            // insert system media db
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, path);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            return context.getContentResolver().insert(photoUri, values);
        } else {
            @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            Uri uri = ContentUris.withAppendedId(photoUri, id);
            cursor.close();
            return uri;
        }
    }

    public ArrayList<String> getFilePaths() {
        FILE_EXTN.add("png");
        FILE_EXTN.add("PNG");
        FILE_EXTN.add("jpg");
        FILE_EXTN.add("JPG");
        FILE_EXTN.add("jpeg");
        FILE_EXTN.add("JPEG");

        ArrayList<String> filePaths = new ArrayList<>();
        File directory = new File(Leaflet_MyApplication.getInstance().GetMainPath());

        if (directory != null && directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null) {
                Arrays.sort(listFiles, (file1, file2) -> {
                    return Long.compare(file1.lastModified(), file2.lastModified());
                });
                for (File listFile : listFiles) {
                    if (IsSupportedFile(listFile.getAbsolutePath())) {
                        filePaths.add(listFile.getAbsolutePath());
                    }
                }
            }
        }
        return filePaths;
    }

    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1)
        );

        return FILE_EXTN.contains(ext.toLowerCase(Locale.getDefault()));

    }

}


