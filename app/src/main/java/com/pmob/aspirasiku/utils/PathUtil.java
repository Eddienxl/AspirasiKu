package com.pmob.aspirasiku.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class PathUtil {

    private static final String TAG = "PathUtil";

    public static String getPath(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(column_index);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting path from URI", e);
            filePath = uri.getPath(); // Fallback to URI path, though often not a file path
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return filePath;
    }
}