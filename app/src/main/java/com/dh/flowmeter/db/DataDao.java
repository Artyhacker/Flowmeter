package com.dh.flowmeter.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.dh.flowmeter.Contract;
import com.dh.flowmeter.DataBean;

import java.util.ArrayList;

/**
 * Created by dh on 17-3-10.
 */

public class DataDao {
    DatabaseOpenHelper myOpenHelper;
    public DataDao(Context context) {
        myOpenHelper = new DatabaseOpenHelper(context, 1);
    }

    public boolean insert(DataBean bean) {
        SQLiteDatabase database = myOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN[0], bean.id);
        values.put(Contract.COLUMN[1], bean.velocity);
        values.put(Contract.COLUMN[2], bean.quantity);
        values.put(Contract.COLUMN[3], bean.cumulant);
        values.put(Contract.COLUMN[4], bean.history);
        long result = database.insert(Contract.TABLE_NAME, null, values);

        database.close();
        return (result != -1)? true: false;
    }

    public int delete(int id) {
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        int result = db.delete(Contract.TABLE_NAME, Contract.COLUMN[0] + "=?", new String[]{id + ""});
        db.close();
        return result;
    }

    public DataBean query(int id) {
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        DataBean result = new DataBean();
        Cursor cursor = db.query(Contract.TABLE_NAME, null, Contract.COLUMN[0] + "=?", new String[]{id + ""}, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int cid = cursor.getInt(0);
                double cvelocity = Double.parseDouble(cursor.getString(1));
                double cquantity = Double.parseDouble(cursor.getString(2));
                String ccumelant = cursor.getString(3);
                String chistory = cursor.getString(4);
                result.id = cid;
                result.velocity = cvelocity;
                result.quantity = cquantity;
                result.cumulant = ccumelant;
                result.history = chistory;
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    public int update(DataBean bean) {
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN[1], bean.velocity);
        values.put(Contract.COLUMN[2], bean.quantity);
        values.put(Contract.COLUMN[3], bean.cumulant);
        values.put(Contract.COLUMN[4], bean.history);
        int result = db.update(Contract.TABLE_NAME, values, Contract.COLUMN[0] + "=?", new String[]{bean.id + ""});
        db.close();
        return result;
    }

    public boolean isEmpty() {
        SQLiteDatabase db = myOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(Contract.TABLE_NAME, null, null, null, null, null, null);
        boolean result = (cursor.getCount() == 0);
        cursor.close();
        db.close();
        return result;
    }

    public void bulkInsert(ArrayList<DataBean> arrayList) {
        SQLiteDatabase database = myOpenHelper.getReadableDatabase();
        for (DataBean bean: arrayList) {
            ContentValues values = new ContentValues();
            values.put(Contract.COLUMN[0], bean.id);
            values.put(Contract.COLUMN[1], bean.velocity);
            values.put(Contract.COLUMN[2], bean.quantity);
            values.put(Contract.COLUMN[3], bean.cumulant);
            values.put(Contract.COLUMN[4], bean.history);
            database.insert(Contract.TABLE_NAME, null, values);
        }
        database.close();
    }
}
