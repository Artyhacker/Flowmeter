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
        myOpenHelper = new DatabaseOpenHelper(context);
    }

    public boolean insert(DataBean bean) {
        SQLiteDatabase database = myOpenHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_ID, bean.id);
        values.put(Contract.COLUMN_NAME, bean.name);
        values.put(Contract.COLUMN_DATE, bean.date);
        values.put(Contract.COLUMN_DATA, bean.data);
        values.put(Contract.COLUMN_UNIT, bean.unit);
        values.put(Contract.COLUMN_MINOR, bean.getMinorStr(bean.minorList));
        values.put(Contract.COLUMN_HISTORY, bean.history);
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
                int cid = cursor.getInt(Contract.COLUMN_INDEX_ID);
                String cname = cursor.getString(Contract.COLUMN_INDEX_NAME);
                String cdate = cursor.getString(Contract.COLUMN_INDEX_DATE);
                double cdata = cursor.getDouble(Contract.COLUMN_INDEX_DATA);
                String cunit = cursor.getString(Contract.COLUMN_INDEX_UNIT);
                String cminor = cursor.getString(Contract.COLUMN_INDEX_MINOR);
                String chistory = cursor.getString(Contract.COLUMN_INDEX_HISTORY);

                result.id = cid;
                result.name = cname;
                result.date = cdate;
                result.data = cdata;
                result.unit = cunit;
                result.minorList = result.getMinorList(cminor);
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
        values.put(Contract.COLUMN_ID, bean.id);
        values.put(Contract.COLUMN_NAME, bean.name);
        values.put(Contract.COLUMN_DATE, bean.date);
        values.put(Contract.COLUMN_DATA, bean.data);
        values.put(Contract.COLUMN_UNIT, bean.unit);
        values.put(Contract.COLUMN_MINOR, bean.getMinorStr(bean.minorList));
        values.put(Contract.COLUMN_HISTORY, bean.history);
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
            values.put(Contract.COLUMN_ID, bean.id);
            values.put(Contract.COLUMN_NAME, bean.name);
            values.put(Contract.COLUMN_DATE, bean.date);
            values.put(Contract.COLUMN_DATA, bean.data);
            values.put(Contract.COLUMN_UNIT, bean.unit);
            values.put(Contract.COLUMN_MINOR, bean.getMinorStr(bean.minorList));
            values.put(Contract.COLUMN_HISTORY, bean.history);
            database.insert(Contract.TABLE_NAME, null, values);
        }
        database.close();
    }
}
