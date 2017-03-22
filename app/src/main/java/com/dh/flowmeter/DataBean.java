package com.dh.flowmeter;

import java.util.ArrayList;

import static com.dh.flowmeter.MainActivity.THRESHOLD;

/**
 * Created by dh on 17-3-7.
 */

public class DataBean {
    public int id;
    //public double velocity;
    //public double quantity;
    //public String cumulant;
    public String history;
    public String name;
    public String date;
    public double data;
    public String unit;

    public ArrayList<Minor> minorList;

    public String getStatus() {
        return (data > Contract.THRESHOLD) ? "正常" : "异常";
    }

    public static class Minor {
        private String key;
        private String value;

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

