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
            Contract.COLUMN[0] + " INTEGER PRIMARY KEY NOT NULL," +
            Contract.COLUMN[1] + " TEXT NOT NULL, " +
            Contract.COLUMN[2] + " TEXT NOT NULL, " +
            Contract.COLUMN[3] + " TEXT NOT NULL, " +
            Contract.COLUMN[4] + " TEXT NOT NULL " +
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
