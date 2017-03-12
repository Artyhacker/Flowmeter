package com.dh.flowmeter;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static final String DATA_URL = "http://10.42.0.1:8080/Flowmeter/data";
    public static final double THRESHOLD = 130;

    private ArrayList<DataBean> dataBeanArrayList;
    private Context mContext;
    private DataDao dataDao;

    @BindView(R.id.list_view)
    ListView lv;

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

        dataDao = new DataDao(mContext);
        dataBeanArrayList = new ArrayList<>();
        getDataByInternet(mContext);

        lv.setOnItemClickListener(this);
    }

    private void getDataByInternet(final Context mContext) {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(DATA_URL)
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
            JSONObject object = new JSONObject(responseStr);
            String strDate = object.getString("date");

            JSONArray array = object.getJSONArray("data");
            //ContentValues[] values = new ContentValues[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject jo = array.getJSONObject(i);
                DataBean bean = new DataBean();
                bean.id = jo.getInt("id");
                bean.velocity = jo.getDouble("velocity");
                bean.quantity = jo.getDouble("quantity");
                bean.cumulant = jo.getString("cumulant");
                bean.history = jo.getString("history");
                //bean.change = String.format("%.2f", bean.quantity - THRESHOLD);
                dataBeanArrayList.add(bean);

                /*
                ContentValues v = new ContentValues();
                v.put(Contract.COLUMN[0], bean.id);
                v.put(Contract.COLUMN[1], bean.velocity);
                v.put(Contract.COLUMN[2], bean.quantity);
                v.put(Contract.COLUMN[3], bean.cumulant);
                v.put(Contract.COLUMN[4], bean.history);
                values[i] = v;*/
            }
            if (dataDao.isEmpty()){
                dataDao.bulkInsert(dataBeanArrayList);
                //mContext.getContentResolver().bulkInsert(Contract.URI, values);
            }
            handler.sendEmptyMessage(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}
