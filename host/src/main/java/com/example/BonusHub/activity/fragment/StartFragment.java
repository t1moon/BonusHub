package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.bonuslib.db.HelperFactory;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;

public class StartFragment extends Fragment {

    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private TextView save_tv;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title;
    private EditText host_description;
    private EditText host_address;

    Toolbar toolbar;

    View rootView;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_start, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_topic_top);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);


        host_title = (EditText) rootView.findViewById(R.id.host_title_et);
        host_description = (EditText) rootView.findViewById(R.id.host_description_et);
        host_address = (EditText) rootView.findViewById(R.id.host_address_et);
        save_tv = (TextView) rootView.findViewById(R.id.save_tv);
        open_time_btn = (Button) rootView.findViewById(R.id.open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.close_time_btn);

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

        save_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = host_title.getText().toString();
                String description = host_description.getText().toString();
                String address = host_address.getText().toString();

                try {
                    HelperFactory.getHelper().getHostDAO().createHost(title, description,
                            address, open_hour, open_minute, close_hour, close_minute);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                goToProfileFragment();
            }
        });

        return rootView;
    }

    public void goToProfileFragment() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new ProfileFragment(), "");
        ft.commit();
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

                        } else {
                            close_time_btn.setTextSize(20);
                            if (minute != 0)
                                close_time_btn.setText(hourOfDay + ":" + minute);
                            else
                                close_time_btn.setText(hourOfDay + ":" + "00");
                            close_hour = hourOfDay;
                            close_minute = minute;
                        }

                    }
                }, 0, 0, false);
        timePickerDialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

}
