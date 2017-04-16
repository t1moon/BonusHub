package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.model.Host;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.sql.SQLException;
import java.util.Calendar;

public class HostEditInfoFragment extends Fragment {

    private int mHour, mMinute;
    private Button save_btn;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title;
    private EditText host_description;
    View rootView;
    public HostEditInfoFragment() {
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

        save_btn = (Button) rootView.findViewById(R.id.save_btn);
        open_time_btn = (Button) rootView.findViewById(R.id.open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.close_time_btn);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                host_title = (EditText) rootView.findViewById(R.id.host_title_et);
                host_description = (EditText) rootView.findViewById(R.id.host_description_et);

                Host host = new Host();
                host.setTitle(host_title.getText().toString());
                host.setDescription(host_description.getText().toString());
                try {
                    HelperFactory.getHelper().getHostDAO().create(host);
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

    private void pickTime(final View v) {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (v == open_time_btn) {
                            open_time_btn.setTextSize(20);
                            open_time_btn.setText("С " + hourOfDay + ":" + minute);
                        } else {
                            close_time_btn.setTextSize(20);
                            close_time_btn.setText("по " + hourOfDay + ":" + minute);
                        }

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


}
