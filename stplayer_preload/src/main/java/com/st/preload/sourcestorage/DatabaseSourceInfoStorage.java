package com.st.preload.sourcestorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.st.preload.bean.SourceInfo;


/**
 * Database based {@link SourceInfoStorage}.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
class DatabaseSourceInfoStorage extends SQLiteOpenHelper implements SourceInfoStorage {

    private static final String TABLE = "SourceInfo";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_MIME = "mime";
    private static final String[] ALL_COLUMNS = new String[]{COLUMN_ID, COLUMN_URL, COLUMN_LENGTH, COLUMN_MIME};
    private static final String CREATE_SQL =
            "CREATE TABLE " + TABLE + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    COLUMN_URL + " TEXT NOT NULL," +
                    COLUMN_MIME + " TEXT," +
                    COLUMN_LENGTH + " INTEGER" +
                    ");";

    DatabaseSourceInfoStorage(@NonNull Context context) {
        super(context, "AndroidVideoCache.db", null, 1);
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Should not be called. There is no any migration");
    }

    @Override
    public @Nullable
    SourceInfo get(@NonNull String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query(TABLE, ALL_COLUMNS, COLUMN_URL + "=?", new String[]{url}, null, null, null);
            return cursor == null || !cursor.moveToFirst() ? null : convert(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void put(@NonNull String url, @NonNull SourceInfo sourceInfo) {
        if (TextUtils.isEmpty(url) || sourceInfo == null) {
            return;
        }
        SourceInfo sourceInfoFromDb = get(url);
        boolean exist = sourceInfoFromDb != null;
        ContentValues contentValues = convert(sourceInfo);
        if (exist) {
            getWritableDatabase().update(TABLE, contentValues, COLUMN_URL + "=?", new String[]{url});
        } else {
            getWritableDatabase().insert(TABLE, null, contentValues);
        }
    }

    @Override
    public void release() {
        close();
    }

    private SourceInfo convert(Cursor cursor) {
        return new SourceInfo(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)),
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_LENGTH)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MIME))
        );
    }

    private ContentValues convert(SourceInfo sourceInfo) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_URL, sourceInfo.url);
        values.put(COLUMN_LENGTH, sourceInfo.length);
        values.put(COLUMN_MIME, sourceInfo.mime);
        return values;
    }
}
