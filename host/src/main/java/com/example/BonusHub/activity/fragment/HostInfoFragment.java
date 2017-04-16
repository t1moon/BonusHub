package com.example.BonusHub.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.activity.activity.MainActivity;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.model.Host;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;
import java.util.List;

public class HostInfoFragment extends Fragment {

    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;


    public HostInfoFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_host_info, container, false);

        host_title = (TextView) rootView.findViewById(R.id.host_title_tv);
        host_description = (TextView) rootView.findViewById(R.id.host_description_tv);
        host_address = (TextView) rootView.findViewById(R.id.host_address_tv);
        host_open_time_tv =(TextView) rootView.findViewById(R.id.host_open_time_tv);
        host_close_time_tv =(TextView) rootView.findViewById(R.id.host_close_time_tv);

        final FloatingActionButton edit = (FloatingActionButton) rootView.findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container_body, new HostEditInfoFragment(), "");
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        Host host = null;

        int host_id = ((MainActivity)getActivity()).getHost_id();
        try {

            host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
            inflate_fields(host);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rootView;
    }

    void inflate_fields(Host host) {

        if (host != null) {
            host_title.setText(host.getTitle());
            host_description.setText(host.getDescription());
            host_address.setText(host.getAddress());
            if (host.getTime_open() % 60 == 0)
                host_open_time_tv.setText(host.getTime_open()/60 + ":" + "00");
            else
                host_open_time_tv.setText(host.getTime_open()/60 + ":" + host.getTime_open() % 60);
            if (host.getTime_close() % 60 == 0)
                host_close_time_tv.setText(host.getTime_close()/60 + ":" + "00");
            else
                host_close_time_tv.setText(host.getTime_close()/60 + ":" + host.getTime_close() % 60);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
