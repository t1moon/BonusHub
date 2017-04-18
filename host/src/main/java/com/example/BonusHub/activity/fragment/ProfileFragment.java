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
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;

public class ProfileFragment extends Fragment {

    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    private int host_id;

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

        host_title = (TextView) rootView.findViewById(R.id.host_title_tv);
        host_description = (TextView) rootView.findViewById(R.id.host_description_tv);
        host_address = (TextView) rootView.findViewById(R.id.host_address_tv);
        host_open_time_tv = (TextView) rootView.findViewById(R.id.host_open_time_tv);
        host_close_time_tv = (TextView) rootView.findViewById(R.id.host_close_time_tv);

        setInfo();

        final FloatingActionButton edit = (FloatingActionButton) rootView.findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditFragment();
            }
        });

        return rootView;
    }

    public void goToEditFragment() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_body, new EditFragment(), "");
        ft.addToBackStack(null);
        ft.commit();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
}
