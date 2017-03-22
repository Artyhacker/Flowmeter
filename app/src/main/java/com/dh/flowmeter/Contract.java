package com.dh.flowmeter;

import android.net.Uri;

import org.w3c.dom.CDATASection;

/**
 * Created by dh on 17-3-10.
 */

public final class Contract {

    public static final String GET_DATA_URL = "http://artyhacker.cn/Flowmeter/data";
    public static final double THRESHOLD = 130;
    public static final String ACTION_DATA_UPDATED = "com.dh.flowmeter.ACTION_DATA_UPDATED";

    //database
    public static final String TABLE_NAME = "DATA";
    public static final String[] COLUMN = {"_id", "NAME", "DATE", "DATA", "UNIT", "MINOR", "HISTORY"};

    public static final int COLUMN_INDEX_ID = 0;
    public static final int COLUMN_INDEX_NAME = 1;
    public static final int COLUMN_INDEX_DATE = 2;
    public static final int COLUMN_INDEX_DATA = 3;
    public static final int COLUMN_INDEX_UNIT = 4;
    public static final int COLUMN_INDEX_MINOR = 5;
    public static final int COLUMN_INDEX_HISTORY = 6;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_DATA = "DATA";
    public static final String COLUMN_UNIT = "UNIT";
    public static final String COLUMN_MINOR = "MINOR";
    public static final String COLUMN_HISTORY = "HISTORY";

    //provider
    public static final String AUTHORITY = "com.dh.flowmeter";
    private static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_DATA = "data";
    public static final String PATH_DATA_WITH_ID = "data/*";
    public static final Uri URI = BASE_URI.buildUpon().appendPath(PATH_DATA).build();

    public static Uri makeUriForId(int id) {
        return URI.buildUpon().appendPath(id + "").build();
    }

    public static String getIDFromUri(Uri queryUri) {
        return queryUri.getLastPathSegment();
    }

    //JSON
    public static final String JSON_ID = "id";
    public static final String JSON_NAME = "name";
    public static final String JSON_DATE ="date";
    public static final String JSON_DATA ="data";
    public static final String JSON_UNIT = "unit";
    public static final String JSON_HISTORY = "history";
    public static final String JSON_MINOR = "minor";
    public static final String JSON_MINOR_KEY = "key";
    public static final String JSON_MINOR_VALUE = "value";
}
