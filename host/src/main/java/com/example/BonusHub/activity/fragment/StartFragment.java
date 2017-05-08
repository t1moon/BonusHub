package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.executors.CreateHostExecutor;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import static android.content.Context.MODE_PRIVATE;

public class StartFragment extends Fragment {
    private static final String LOGIN_PREFERENCES = "LoginData";

    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title;
    private EditText host_description;
    private EditText host_address;
    private static int host_id;
    private LogInActivity logInActivity;

    View rootView;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreateHostExecutor.getInstance().setCallback(new CreateHostExecutor.Callback() {
            @Override
            public void onCreated(int host_id) {
                onHostCreated(host_id);
            }
        });
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_start, container, false);

        host_title = (EditText) rootView.findViewById(R.id.host_title_et);
        host_description = (EditText) rootView.findViewById(R.id.host_description_et);
        host_address = (EditText) rootView.findViewById(R.id.host_address_et);
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

        setHasOptionsMenu(true);

        return rootView;
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.continue_btn:
                String title = host_title.getText().toString();
                String description = host_description.getText().toString();
                String address = host_address.getText().toString();
                if (title.equals(""))
                    host_title.setError("Введите название");
                else if (description.equals(""))
                    host_description.setError("Введите описание");
                else if (address.equals(""))
                    host_address.setError("Введите адрес");
                else {
                    Host host = new Host(title, description, address);
                    host.setTime_open(open_hour * 60 + open_minute);
                    host.setTime_close(close_hour * 60 + close_minute);
                    host.setProfile_image(null);
                    CreateHostExecutor.getInstance().createHost(host);

                    AuthUtils.setHosted(getActivity());
                    goToMainActivity();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void onHostCreated(int host_id) {
        getActivity().getPreferences(MODE_PRIVATE).edit()
                .putInt("host_id", host_id).apply();
    }



}
