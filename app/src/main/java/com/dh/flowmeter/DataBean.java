package com.dh.flowmeter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

import static com.dh.flowmeter.MainActivity.THRESHOLD;

/**
 * Created by dh on 17-3-7.
 */

public class DataBean {
    public int id;
    public String history;
    public String name;
    public String date;
    public double data;
    public String unit;

    public ArrayList<Minor> minorList;

    private float[] getThreshold(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.threshold_sp_name), Context.MODE_PRIVATE);
        float[] result = new float[2];
        result[0] = sp.getFloat(id + "low", -1);
        result[1] = sp.getFloat(id + "high", -1);
        return result;
    }

    public String getStatus(Context context) {

        float[] threshold = getThreshold(context);
        if (threshold[0] == -1 && threshold[1] == -1)
            return "正常";
        else if (threshold[0] == -1)
            return (data <= threshold[1]) ? "正常" : "异常";
        else if (threshold[1] == -1)
            return (data >= threshold[0]) ? "正常" : "异常";
        else
            return (data >= threshold[0] && data <= threshold[1]) ? "正常" : "异常";
    }

    public static class Minor {
        public String key;
        public String value;

        public Minor(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public String getMinorStr(ArrayList<Minor> minorList) {
        if (!minorList.isEmpty()){
            StringBuilder builder = new StringBuilder();
            for (Minor minor : minorList) {
                builder.append(minor.key).append(" ").append(minor.value).append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            return builder.toString();
        }
        return null;
    }

    public ArrayList<Minor> getMinorList(String minorStr) {
        if (!minorStr.equals("")) {
            ArrayList<Minor> minorArrayList = new ArrayList<>();
            String[] sminorStr = minorStr.split(",");
            for (String ssminorStr : sminorStr) {
                String[] sssminorStr = ssminorStr.split(" ");
                if (!sssminorStr.equals("")) {
                    minorArrayList.add(new Minor(sssminorStr[0], sssminorStr[1]));
                }
            }
            return minorArrayList;
        }
        return null;
    }


    /*
    public String getDeviationStr(){
        return String.format("%.2f", getDeviation());
    }

    public double getDeviation() {
        return (quantity - THRESHOLD);
    }*/
}

