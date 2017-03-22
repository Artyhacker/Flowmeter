package com.dh.flowmeter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.flowmeter.db.DataDao;
import com.dh.flowmeter.db.DatabaseOpenHelper;
import com.dh.flowmeter.sync.DataSyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dh on 17-3-7.
 */

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static final double THRESHOLD = 130;

    private ArrayList<DataBean> dataBeanArrayList;
    private Context mContext;
    private DataDao dataDao;
    private ContentResolver resolver;

    @BindView(R.id.list_view)
    ListView lv;
    @BindView(R.id.empty_view)
    TextView emptyView;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            DataAdapter adapter = new DataAdapter(mContext, dataBeanArrayList);
            lv.setAdapter(adapter);
            return true;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;
        resolver = getContentResolver();

        //DataUpdateBroadcast broadcast = new DataUpdateBroadcast();

        dataDao = new DataDao(mContext);
        dataBeanArrayList = new ArrayList<>();
        getDataByInternet(mContext);

        lv.setEmptyView(emptyView);
        lv.setOnItemClickListener(this);

        //DataSyncAdapter.initializeSyncAdapter(mContext);
    }


    private void getDataByInternet(final Context mContext) {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(Contract.GET_DATA_URL)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                parseJSON(responseStr);
            }
        });
    }

    private void parseJSON(String responseStr) {
        try {
            JSONArray jsonArray = new JSONArray(responseStr);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject bean = jsonArray.getJSONObject(i);

                int jsonID = bean.getInt(Contract.JSON_ID);
                String jsonName = bean.getString(Contract.JSON_NAME);
                String jsonDate = bean.getString(Contract.JSON_DATE);
                double jsonData = bean.getDouble(Contract.JSON_DATA);
                String jsonUnit = bean.getString(Contract.JSON_UNIT);
                String jsonHistory = bean.getString(Contract.JSON_HISTORY);

                ArrayList<DataBean.Minor> minorList = new ArrayList<>();
                //Log.e("MainActivity", "name: " + jsonName + ", date: " + jsonDate + ", data: " + jsonData + ", unit: " + jsonUnit);
                JSONArray jsonMinors = bean.getJSONArray(Contract.JSON_MINOR);
                for (int j = 0; j < jsonMinors.length(); j++) {
                    JSONObject jsonMinor = jsonMinors.getJSONObject(j);
                    String jsonKey = jsonMinor.getString(Contract.JSON_MINOR_KEY);
                    String jsonValue = jsonMinor.getString(Contract.JSON_MINOR_VALUE);
                    DataBean.Minor minor = new DataBean.Minor(jsonKey, jsonValue);
                    minorList.add(minor);
                }

                DataBean dataBean = new DataBean();
                dataBean.id = jsonID;
                dataBean.name = jsonName;
                dataBean.date = jsonDate;
                dataBean.data = jsonData;
                dataBean.unit = jsonUnit;
                dataBean.history = jsonHistory;
                dataBean.minorList = minorList;

                dataBeanArrayList.add(dataBean);
            }

            if (dataDao.isEmpty()){
                dataDao.bulkInsert(dataBeanArrayList);
                //mContext.getContentResolver().bulkInsert(Contract.URI, values);

                Intent intent = new Intent(Contract.ACTION_DATA_UPDATED);
                sendBroadcast(intent);
            }
            handler.sendEmptyMessage(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("id", (int)id);
        startActivity(intent);
    }

    private class DataUpdateBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Contract.ACTION_DATA_UPDATED.equals(intent.getAction())) {
                //getDataFromDB();
            }
        }
    }

    /**
     * public int id;
     public double velocity;
     public double quantity;
     public String cumulant;
     public String history;
     */
    /*
    private void getDataFromDB() {
        dataBeanArrayList = new ArrayList<>();
        Cursor cursor = resolver.query(Contract.URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int cid = cursor.getInt(0);
                double cvelocity = cursor.getDouble(1);
                double cquantity = cursor.getDouble(2);
                String ccumulant = cursor.getString(3);
                String chistory = cursor.getString(4);

                DataBean bean = new DataBean();
                bean.id = cid;
                bean.velocity = cvelocity;
                bean.history = chistory;
                bean.cumulant = ccumulant;
                bean.quantity = cquantity;

                dataBeanArrayList.add(bean);
            }
            cursor.close();
            handler.sendEmptyMessage(1);
        }
    }*/

}
