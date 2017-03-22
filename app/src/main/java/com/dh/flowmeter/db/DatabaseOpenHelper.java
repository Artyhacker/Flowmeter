package com.dh.flowmeter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dh.flowmeter.Contract;

/**
 * Created by dh on 17-3-10.
 */

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private String sql = "CREATE TABLE " + Contract.TABLE_NAME + "(" +
            Contract.COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL," +
            Contract.COLUMN_NAME + " TEXT NOT NULL, " +
            Contract.COLUMN_DATE + " TEXT NOT NULL, " +
            Contract.COLUMN_DATA + " TEXT NOT NULL, " +
            Contract.COLUMN_UNIT + " TEXT NOT NULL, " +
            Contract.COLUMN_MINOR + " TEXT NOT NULL, " +
            Contract.COLUMN_HISTORY + " TEXT NOT NULL " +
            ")";

    public DatabaseOpenHelper(Context context) {
        super(context, "data", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.delete(Contract.TABLE_NAME, null, null);
        db.execSQL(sql);
    }
}
