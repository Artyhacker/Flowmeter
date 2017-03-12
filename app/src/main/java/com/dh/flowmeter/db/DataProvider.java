package com.dh.flowmeter.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dh.flowmeter.Contract;

/**
 * Created by dh on 17-3-12.
 */

public class DataProvider extends ContentProvider {

    static final int DATA = 100;
    static final int DATA_FOR_ID = 101;
    static UriMatcher uriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_DATA, DATA);
        matcher.addURI(Contract.AUTHORITY, Contract.PATH_DATA_WITH_ID, DATA_FOR_ID);
        return matcher;
    }

    private DatabaseOpenHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseOpenHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor returnCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case DATA:
                returnCursor = db.query(Contract.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case DATA_FOR_ID:
                returnCursor = db.query(Contract.TABLE_NAME, projection, Contract.COLUMN[0] + "=?", new String[]{Contract.getIDFromUri(uri)}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknow URI: " + uri);
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case DATA:
                db.insert(
                        Contract.TABLE_NAME,
                        null,
                        values
                );
                returnUri = Contract.URI;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatcher.match(uri)) {
            case DATA:
                rowsDeleted = db.delete(
                        Contract.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            case DATA_FOR_ID:
                String id = Contract.getIDFromUri(uri);
                rowsDeleted = db.delete(Contract.TABLE_NAME, Contract.COLUMN[0] + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI:" + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case DATA:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        db.insert(
                                Contract.TABLE_NAME,
                                null,
                                value
                        );
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
