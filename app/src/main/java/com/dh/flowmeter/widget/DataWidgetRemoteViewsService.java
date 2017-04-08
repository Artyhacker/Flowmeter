package com.dh.flowmeter.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dh.flowmeter.Contract;
import com.dh.flowmeter.DataBean;
import com.dh.flowmeter.DetailActivity;
import com.dh.flowmeter.R;

/**
 * Created by dh on 17-3-13.
 */

public class DataWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.URI, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_data_item);

                DataBean bean = new DataBean();
                bean.id = data.getInt(Contract.COLUMN_INDEX_ID);
                bean.data = data.getDouble(Contract.COLUMN_INDEX_DATA);
                bean.unit = data.getString(Contract.COLUMN_INDEX_UNIT);
                String status = bean.getStatus(getApplicationContext());

                views.setTextViewText(R.id.widget_item_id, bean.id + "");
                views.setTextViewText(R.id.widget_item_quantity, bean.data + " " + bean.unit);
                views.setTextViewText(R.id.widget_item_change, status);

                final Intent fillInIntent = new Intent(getApplicationContext(), DetailActivity.class);
                fillInIntent.putExtra("id", bean.id);
                Log.d("WidgetService", "id: " + bean.id);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_data_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(0);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
