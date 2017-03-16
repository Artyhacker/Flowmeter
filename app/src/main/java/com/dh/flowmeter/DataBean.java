package com.dh.flowmeter;

import static com.dh.flowmeter.MainActivity.THRESHOLD;

/**
 * Created by dh on 17-3-7.
 */

public class DataBean {
    public int id;
    public double velocity;
    public double quantity;
    public String cumulant;
    public String history;

    public String getDeviationStr(){
        return String.format("%.2f", getDeviation());
    }

    public double getDeviation() {
        return (quantity - THRESHOLD);
    }
}
