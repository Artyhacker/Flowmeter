package com.dh.flowmeter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dh on 17-3-7.
 */

public class DataAdapter extends BaseAdapter {

    private ArrayList<DataBean> dataBeanList;
    private Context context;

    public DataAdapter(Context context,  ArrayList<DataBean> dataBeanList) {
        this.context = context;
        this.dataBeanList = dataBeanList;
    }

    @Override
    public int getCount() {
        return dataBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataBeanList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder viewholder;
        if (convertView != null) {
            viewholder = (Viewholder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.list_item, null);

            viewholder = new Viewholder();
            viewholder.tvId = (TextView) convertView.findViewById(R.id.tv_id_item);
            viewholder.tvData = (TextView) convertView.findViewById(R.id.tv_data_item);
            viewholder.tvChange = (TextView) convertView.findViewById(R.id.tv_change_item);
            convertView.setTag(viewholder);
        }

        viewholder.tvId.setText(dataBeanList.get(position).id + "");
        viewholder.tvData.setText(dataBeanList.get(position).quantity + "");
        viewholder.tvChange.setText(dataBeanList.get(position).getDeviationStr());

        if (dataBeanList.get(position).getDeviation() >= 0){
            viewholder.tvChange.setBackgroundResource(R.drawable.change_pill_green);
        } else {
            viewholder.tvChange.setBackgroundResource(R.drawable.change_pill_red);
        }

        return convertView;
    }

    private class Viewholder{
        TextView tvId;
        TextView tvData;
        TextView tvChange;
    }
}
