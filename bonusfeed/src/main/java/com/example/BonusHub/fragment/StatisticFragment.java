package com.example.BonusHub.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.activity.LogInActivity;
import com.example.BonusHub.retrofit.statistic.StatisticResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.timur.BonusHub.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the { ScanQrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment implements NetworkThread.ExecuteCallback<StatisticResponse>, OnChartValueSelectedListener {

    HostMainActivity hostMainActivity;
    TableLayout tl;
    GraphView graph;
    private PieChart mChart;
    private Integer statisticCallbackId;

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hostMainActivity = (HostMainActivity) getActivity();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (statisticCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(statisticCallbackId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

//        graph = (GraphView) rootView.findViewById(R.id.graph);
//
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
//        DataPoint[] dataPoints = new DataPoint[];
//        graph.addSeries(series);
        mChart = (PieChart) rootView.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

//        mChart.setCenterTextTypeface(mTfLight);
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setOnChartValueSelectedListener(this);
        setData(4, 100);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
//        mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);


//        tl = (TableLayout) rootView.findViewById(R.id.statistic_table);
//        Toast.makeText(hostMainActivity, "Для статистики вам нужны ещё данные", Toast.LENGTH_SHORT).show();
//        makeTableRow("01.06.2017", 330, 150, 410);
//        makeTableRow("02.06.2017", 540, 200, 220);
//        makeTableRow("03.06.2017", 690, 250, 330);
//        makeTableRow("04.06.2017", 800, 230, 560);
//        final HostApiInterface apiInterface = RetrofitFactory.retrofitHost().create(HostApiInterface.class);
//        Call<StatisticResponse> call = apiInterface.getStatistic(AuthUtils.getCookie(hostMainActivity));
//        if (statisticCallbackId == null) {
//            statisticCallbackId = NetworkThread.getInstance().registerCallback(this);
//            NetworkThread.getInstance().execute(call, statisticCallbackId);
//        }
        return rootView;
    }
    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Продажи сотрудников");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, s.length(), 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        String[] mParties = new String[] {
                "Масалимов", "Алиев", "Туркевич", "Мазырин"
        };
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        entries.add(new PieEntry(10f, "Масалимов"));
        entries.add(new PieEntry(2f, "Алиев"));
        entries.add(new PieEntry(6f, "Туркевич"));
        entries.add(new PieEntry(5f, "Аквазырин"));

        PieDataSet dataSet = new PieDataSet(entries, "Сотрудники");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
//        data.setValueTypeface(mTfLight);//
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showResponse(StatisticResponse result) {

        List<DataPoint> pointList = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<StatisticResponse.Operation> operationList = result.getOperationList();
        for (StatisticResponse.Operation operation : operationList) {

            Calendar calendar = getDate(operation.getDate());
            Date d = calendar.getTime();
            dates.add(Integer.toString(d.getDate()));
            pointList.add(new DataPoint(d.getDate(), operation.getAvgBill()));
            makeTableRow(getReadableDate(operation.getDate()), operation.getAvgBill(), operation.getIncome(), operation.getOutcome());
        }
        String[] axisX = new String[dates.size()];
        axisX = dates.toArray(axisX);
        if (axisX.length > 1) {
            DataPoint[] dataPoints = new DataPoint[pointList.size()];
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(pointList.toArray(dataPoints));
            graph.addSeries(series);

            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);

            staticLabelsFormatter.setHorizontalLabels(axisX);
            graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

            graph.getViewport().setMinX(Integer.parseInt(dates.get(0)));
            graph.getViewport().setMaxX(Integer.parseInt(dates.get(dates.size() - 1)));
            graph.getViewport().setXAxisBoundsManual(true);
        }
        else {
            graph.setVisibility(View.GONE);
            Toast.makeText(hostMainActivity, "Для статистики вам нужны ещё данные", Toast.LENGTH_SHORT).show();
        }
    }

    private String getReadableDate(String date) {
        SimpleDateFormat src = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        SimpleDateFormat dsn = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
        Date result = null;
        try {
            result = src.parse(date);
        } catch (ParseException e) {
            Log.d("Exception", e.getMessage());
        }
        return dsn.format(result);
    }

    private void makeTableRow(String date, float avgBill, int income, int outcome) {
        TableRow tr;
        TextView date_tv, avg_bill_tv, income_tv, outcome_tv;

        tr = new TableRow(hostMainActivity.getApplicationContext());

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        avg_bill_tv = new TextView(hostMainActivity.getApplicationContext());
        date_tv = new TextView(hostMainActivity.getApplicationContext());
        income_tv = new TextView(hostMainActivity.getApplicationContext());
        outcome_tv = new TextView(hostMainActivity.getApplicationContext());

        date_tv.setText(date);
        date_tv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        date_tv.setGravity(Gravity.CENTER);
        date_tv.setTextColor(Color.BLACK);
        date_tv.setWidth(0);

        avg_bill_tv.setText(String.format("%.2f", avgBill));
        avg_bill_tv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        avg_bill_tv.setGravity(Gravity.CENTER);
        avg_bill_tv.setTextColor(Color.BLACK);
        avg_bill_tv.setWidth(0);

        income_tv.setText(Integer.toString(income));
        income_tv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        income_tv.setGravity(Gravity.CENTER);
        income_tv.setTextColor(Color.BLACK);
        income_tv.setWidth(0);

        outcome_tv.setText(Integer.toString(outcome));
        outcome_tv.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT, 1f));
        outcome_tv.setGravity(Gravity.CENTER);
        outcome_tv.setTextColor(Color.BLACK);
        outcome_tv.setWidth(0);

        tr.addView(date_tv);
        tr.addView(avg_bill_tv);
        tr.addView(income_tv);
        tr.addView(outcome_tv);
        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }


    private void showError(Throwable error) {
        new AlertDialog.Builder(getActivity())
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }

    public Calendar getDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        Date date = null;
        Calendar calendar = Calendar.getInstance();
        try {
            date = sdf.parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    @Override
    public void onResponse(Call<StatisticResponse> call, Response<StatisticResponse> response) {
    }

    @Override
    public void onFailure(Call<StatisticResponse> call, Response<StatisticResponse> response) {
        if (response.body() != null)
            Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_SHORT).show();
        AuthUtils.logout(getActivity());
        goToLogin();
    }

    @Override
    public void onSuccess(StatisticResponse result) {
        showResponse(result);
    }

    @Override
    public void onError(Exception ex) {
        showError(ex);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
