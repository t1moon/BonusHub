package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;

public class EditFragment extends Fragment {

    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private boolean set_open_time = false, set_close_time = false;
    private TextView save_tv;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title_et;
    private EditText host_description_et;
    private EditText host_address_et;
    View rootView;
    int host_id;

    Toolbar toolbar;
    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        set_open_time = false;
        set_close_time = false;

        host_title_et = (EditText) rootView.findViewById(R.id.edit_host_title_et);
        host_description_et = (EditText) rootView.findViewById(R.id.edit_host_description_et);
        host_address_et = (EditText) rootView.findViewById(R.id.edit_host_address_et);
        open_time_btn = (Button) rootView.findViewById(R.id.edit_open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.edit_close_time_btn);
        save_tv = (TextView) rootView.findViewById(R.id.edit_save_tv);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_edit);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        setInfo();

        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Host host = null;
                try {

                    UpdateBuilder<Host, Integer> updateBuilder = HelperFactory.getHelper().
                            getHostDAO().updateBuilder();

                    updateBuilder.where().eq("Id", host_id);

                    updateBuilder.updateColumnValue("title", host_title_et.getText());
                    updateBuilder.updateColumnValue("description", host_description_et.getText());
                    updateBuilder.updateColumnValue("address", host_address_et.getText());
                    if (set_open_time)
                        updateBuilder.updateColumnValue("time_open", open_hour * 60 + open_minute);
                    if (set_close_time)
                        updateBuilder.updateColumnValue("time_close", close_hour * 60 + close_minute);

                    updateBuilder.update();


                } catch (SQLException e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStackImmediate();

            }
        });

        open_time_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickTime(v);
            }
        });

        close_time_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pickTime(v);
            }
        });
        return rootView;
    }

    public void setInfo() {
        host_id = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("host_id", -1);
        Host host = null;
        try {
            host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (host != null) {
            String title = host.getTitle();
            String description = host.getDescription();
            String address = host.getAddress();
            int open_hour = host.getTime_open() / 60;
            int open_minute = host.getTime_open() % 60;
            int close_hour = host.getTime_close() / 60;
            int close_minute = host.getTime_close() % 60;

            host_title_et.setText(title);
            host_description_et.setText(description);
            host_address_et.setText(address);
            if (open_minute != 0)
                open_time_btn.setText(open_hour + ":" + open_minute);
            else
                open_time_btn.setText(open_hour + ":" + "00");
            if (close_minute != 0)
                close_time_btn.setText(close_hour + ":" + close_minute);
            else
                close_time_btn.setText(close_hour + ":" + "00");
        }
    }

    private void pickTime(final View v) {
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (v == open_time_btn) {
                            open_time_btn.setTextSize(20);
                            if (minute != 0)
                                open_time_btn.setText(hourOfDay + ":" + minute);
                            else
                                open_time_btn.setText(hourOfDay + ":" + "00");
                            open_hour = hourOfDay;
                            open_minute = minute;
                            set_open_time = true;
                        } else {
                            close_time_btn.setTextSize(20);
                            if (minute !=0)
                                close_time_btn.setText(hourOfDay + ":" + minute);
                            else
                                close_time_btn.setText(hourOfDay + ":" + "00");
                            close_hour = hourOfDay;
                            close_minute = minute;
                            set_close_time = true;
                        }

                    }
                }, 0, 0, false);
        timePickerDialog.show();
    }


}
