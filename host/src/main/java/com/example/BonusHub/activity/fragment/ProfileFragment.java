package com.example.BonusHub.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.BonusHub.activity.manager.HostInfoView;
import com.example.BonusHub.activity.manager.HostManager;
import com.example.BonusHub.activity.manager.HostInfoManager;
import com.example.timur.BonusHub.R;

public class ProfileFragment extends Fragment implements HostInfoView {

    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;

    private HostManager hostManager;

    public ProfileFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        hostManager = new HostInfoManager(this, getActivity());

        host_title = (TextView) rootView.findViewById(R.id.host_title_tv);
        host_description = (TextView) rootView.findViewById(R.id.host_description_tv);
        host_address = (TextView) rootView.findViewById(R.id.host_address_tv);
        host_open_time_tv = (TextView) rootView.findViewById(R.id.host_open_time_tv);
        host_close_time_tv = (TextView) rootView.findViewById(R.id.host_close_time_tv);

        final FloatingActionButton edit = (FloatingActionButton) rootView.findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container_body, new HostEditInfoFragment(), "");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        hostManager.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setInfo(String title, String description, String address, int open_hour, int open_minute, int close_hour, int close_minute) {
        host_title.setText(title);
        host_description.setText(description);
        host_address.setText(address);
        if (open_minute != 0)
            host_open_time_tv.setText(open_hour + ":" + open_minute);
        else
            host_open_time_tv.setText(open_hour + ":" + "00");
        if (close_minute != 0)
            host_close_time_tv.setText(close_hour + ":" + close_minute);
        else
            host_close_time_tv.setText(close_hour + ":" + "00");
    }
}
