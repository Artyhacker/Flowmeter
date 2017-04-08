package com.dh.flowmeter.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;

import com.dh.flowmeter.Contract;
import com.dh.flowmeter.DataBean;
import com.dh.flowmeter.R;
import com.dh.flowmeter.db.DataDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dh on 17-3-17.
 */

public class DataSyncAdapter extends AbstractThreadedSyncAdapter {

    private ContentResolver resolver;
    private Context context;
    private DataDao dataDao;
    private ArrayList<DataBean> dataBeanArrayList;

    public DataSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        resolver = getContext().getContentResolver();
        context = getContext();
        dataDao = new DataDao(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        resolver = getContext().getContentResolver();
        dataBeanArrayList = new ArrayList<>();
        getDataByInternet(context);
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
                context.sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();

        //将此同步放在同步请求队列前面，立即进行同步而不延迟
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        //忽略当前设置强制发起同步
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        DataSyncAdapter.configurePeriodicSync(context);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context) {
        long pollFrequency = 1;
        long syncInterval = 60 * 60 * pollFrequency;
        long flexTime = syncInterval/3;
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static SharedPreferences getDefaultSharedPreferencesMultiprocess(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences",
                Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
    }
}
