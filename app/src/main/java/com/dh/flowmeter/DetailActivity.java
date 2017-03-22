package com.dh.flowmeter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dh.flowmeter.db.DataDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by dh on 17-3-10.
 */

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.chart_view)
    LineChartView lineChart;
    @BindView(R.id.detail_data_des)
    TextView tvDes;
    @BindView(R.id.detail_cumulant)
    TextView tvCumulant;
    @BindView(R.id.detail_velocity)
    TextView tvVelocity;

    List<PointValue> mPointValues;
    List<AxisValue> mAxisValues;

    private DataDao dataDao;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        dataDao = new DataDao(this);

        if (id != -1) {
            drawChart(id);
        }
    }

    private void drawChart(long id) {
        DataBean bean = dataDao.query((int) id);
        //tvVelocity.setText("当前流速: " + bean.velocity + " m/s");
        //tvCumulant.setText("当天净累积量: " + bean.cumulant + " m³");
        if (bean.history != null) {
            mAxisValues = new ArrayList<AxisValue>();
            mPointValues = new ArrayList<PointValue>();
            String[] historyStr = bean.history.split(" ");
            for (int i = 0; i < historyStr.length; i++) {
                mAxisValues.add(new AxisValue(i).setLabel(i + "hour"));
                mPointValues.add(new PointValue(i, Integer.parseInt(historyStr[i])));
            }
        }
        initLineChart();
        lineChart.setOnValueTouchListener(new ValueTouchListener());
    }

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);

        lines.add(line);
        final LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);
//        axisX.setName("DATE");
        axisX.setTextSize(10);
        axisX.setMaxLabelChars(8);
        axisX.setValues(mAxisValues);
        data.setAxisXBottom(axisX);
        axisX.setHasLines(true);

        // Y轴是根据数据的大小自动设置Y轴上限
        Axis axisY = new Axis();  //Y轴
        //axisY.setName("流量");
        //axisY.setTextSize(10);
        //data.setAxisYLeft(axisY);
        axisY.setMaxLabelChars(6);
        List<AxisValue> values = new ArrayList<>();
        for (int i = 100; i < 200; i += 10) {
            AxisValue value = new AxisValue(i);
            String label = i + "";
            value.setLabel(label);
            values.add(value);
        }
        axisY.setValues(values);
        data.setAxisYLeft(axisY);

        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 5);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right= 7;
        lineChart.setCurrentViewport(v);
    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            tvDes.setText("时间: " + pointIndex + " 时, 流量: " + (int) value.getY() + " m³");
        }

        @Override
        public void onValueDeselected() {
        }
    }
}
