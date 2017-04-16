package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.BonusHub.activity.activity.MainActivity;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.model.Host;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.sql.SQLException;
import java.util.Calendar;

public class HostStartInfoFragment extends Fragment {

    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private boolean set_open_time = false, set_close_time = false;
    private Button save_btn;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title;
    private EditText host_description;
    private EditText host_address;
    View rootView;
    public HostStartInfoFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_host_edit_info, container, false);

        set_open_time = false;
        set_close_time = false;
        save_btn = (Button) rootView.findViewById(R.id.save_btn);
        open_time_btn = (Button) rootView.findViewById(R.id.open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.close_time_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                host_title = (EditText) rootView.findViewById(R.id.host_title_et);
                host_description = (EditText) rootView.findViewById(R.id.host_description_et);
                host_address = (EditText) rootView.findViewById(R.id.host_address_et);

                Host host = new Host();
                host.setTitle(host_title.getText().toString());
                host.setDescription(host_description.getText().toString());
                host.setAddress(host_address.getText().toString());
                if (set_open_time && set_close_time) {
                    host.setTime_open(open_hour * 60 + open_minute);
                    host.setTime_close(close_hour*60 + close_minute);
                }

                int host_id = 0;
                try {
                    HelperFactory.getHelper().getHostDAO().create(host);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                host_id = host.getId();
                ((MainActivity)getActivity()).setHost_id(host_id);

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container_body, new HostInfoFragment(), "");
                ft.commit();

//                Toast.makeText(getContext(), Integer.toString(host_id), Toast.LENGTH_SHORT).show();
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.popBackStackImmediate();
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
