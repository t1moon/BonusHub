package com.example.BonusHub.activity.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.retrofit.ApiInterface;
import com.example.BonusHub.activity.retrofit.NetworkThread;
import com.example.BonusHub.activity.retrofit.RetrofitFactory;
import com.example.BonusHub.activity.retrofit.statistic.StatisticResponse;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsPojo;
import com.example.BonusHub.activity.retrofit.updatePoints.UpdatePointsResponse;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the { ScanQrFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    MainActivity mainActivity;
    TableLayout tl;
    GraphView graph;

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistic, container, false);

        graph = (GraphView) rootView.findViewById(R.id.graph);
        tl = (TableLayout) rootView.findViewById(R.id.statistic_table);

        final ApiInterface apiInterface = RetrofitFactory.retrofitHost().create(ApiInterface.class);
        Call<StatisticResponse> call = apiInterface.getStatistic(AuthUtils.getCookie(mainActivity));
        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<StatisticResponse>() {
            @Override
            public void onSuccess(StatisticResponse result) {
                showResponse(result);
            }

            @Override
            public void onError(Exception ex) {
                showError(ex);
            }
        });
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
            Toast.makeText(mainActivity, "Для статистики вам нужны ещё данные", Toast.LENGTH_SHORT).show();
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

        tr = new TableRow(mainActivity.getApplicationContext());

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
        avg_bill_tv = new TextView(mainActivity.getApplicationContext());
        date_tv = new TextView(mainActivity.getApplicationContext());
        income_tv = new TextView(mainActivity.getApplicationContext());
        outcome_tv = new TextView(mainActivity.getApplicationContext());

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
}
