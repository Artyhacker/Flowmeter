package com.dh.flowmeter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.chart_view)
    LineChartView lineChart;
    @BindView(R.id.detail_data_des)
    TextView tvDes;
    @BindView(R.id.detail_minor_1)
    TextView tvMinor1;
    @BindView(R.id.detail_minor_2)
    TextView tvMinor2;
    @BindView(R.id.detail_minor_3)
    TextView tvMinor3;
    @BindView(R.id.detail_threshold_btn)
    Button btnThreshold;


    List<PointValue> mPointValues;
    List<AxisValue> mAxisValues;
    private Context mContext;
    private int id;
    private SharedPreferences sp;

    private DataDao dataDao;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mContext = this;
        sp = getSharedPreferences(getString(R.string.threshold_sp_name), MODE_PRIVATE);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        dataDao = new DataDao(this);
        btnThreshold.setOnClickListener(this);

        float thresholdLow = sp.getFloat(id+"low", -1);
        float thresholdHigh = sp.getFloat(id+"high", -1);
        if (thresholdLow == -1 && thresholdHigh == -1) {
            btnThreshold.setText("点击设定阈值");
        } else if (thresholdLow != -1 && thresholdHigh != -1) {
            btnThreshold.setText("阈值范围: " + thresholdLow + " ~ " + thresholdHigh);
        } else if (thresholdLow != -1) {
            btnThreshold.setText("低阈值: " + thresholdLow);
        } else {
            btnThreshold.setText("高阈值: " + thresholdHigh);
        }

        if (id != -1) {
            DataBean bean = dataDao.query(id);
            if (bean != null) {

                ArrayList<DataBean.Minor> minorArrayList = bean.minorList;
                if (minorArrayList != null) {
                    for (int i = 0; i < minorArrayList.size(); i++) {
                        switch (i) {
                            case 0:
                                DataBean.Minor minor1 = minorArrayList.get(0);
                                tvMinor1.setText(minor1.key + ": " + minor1.value);
                                break;
                            case 1:
                                DataBean.Minor minor2 = minorArrayList.get(1);
                                tvMinor2.setText(minor2.key + ": " + minor2.value);
                                break;
                            default:
                                break;
                        }
                    }

                    /*
                    if (minorArrayList.size() > 2) {
                        tvMinor3.setVisibility(View.VISIBLE);
                        StringBuilder builder = new StringBuilder();
                        for (int j = 2; j < minorArrayList.size(); j++) {
                            DataBean.Minor minor3 = minorArrayList.get(j);
                            builder.append(minor3.key).append(": ").append(minor3.value).append("\n");
                        }
                        tvMinor3.setText(builder.toString());
                    }*/
                }

                drawChart(bean);
            }
        }
    }

    private void drawChart(DataBean bean) {
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.detail_threshold_btn) {
            createDialog();
        }
    }

    private void createDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View viewDialog = LayoutInflater.from(mContext).inflate(R.layout.dialog_shreshold, null);
        //final EditText etThreshold = (EditText) viewDialog.findViewById(R.id.et_threshold_dialog);
        final EditText etThresholdLow = (EditText) viewDialog.findViewById(R.id.et_threshold_low);
        final EditText etThresholdHigh = (EditText) viewDialog.findViewById(R.id.et_threshold_high);
        builder.setTitle("阈值设定")
                .setIcon(R.mipmap.ic_launcher)
                .setView(viewDialog);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String strThreshold = etThreshold.getText().toString();
                String strLow = etThresholdLow.getText().toString();
                String strHigh = etThresholdHigh.getText().toString();
                float low = -1;
                float high = -1;
                SharedPreferences.Editor editor = sp.edit();
                if (!strLow.equals("")) {
                    low = Float.parseFloat(strLow);
                    editor.putFloat(id + "low", low);
                    editor.apply();
                }
                if (!strHigh.equals("")) {
                    high = Float.parseFloat(strHigh);
                    editor.putFloat(id + "high", high);
                    editor.apply();
                }

                if (low == -1 && high == -1) {
                    btnThreshold.setText("点击设定阈值");
                } else if (low != -1 && high != -1) {
                    btnThreshold.setText("阈值范围: " + low + " ~ " + high);
                } else if (low != -1) {
                    btnThreshold.setText("低阈值: " + low);
                } else {
                    btnThreshold.setText("高阈值: " + high);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            tvDes.setText("时间: " + pointIndex + " 时, 数据: " + (int) value.getY() + " m³");
        }

        @Override
        public void onValueDeselected() {
        }
    }
}
