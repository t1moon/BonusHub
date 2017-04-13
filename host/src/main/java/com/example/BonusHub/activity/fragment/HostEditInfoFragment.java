package com.example.BonusHub.activity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.model.Host;
import com.example.timur.BonusHub.R;
import com.google.zxing.integration.android.IntentIntegrator;

import java.sql.SQLException;

public class HostEditInfoFragment extends Fragment {

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

        final View rootView = inflater.inflate(R.layout.fragment_host_edit_info, container, false);

        final Button save_btn = (Button) rootView.findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText host_title = (EditText) rootView.findViewById(R.id.host_title_et);
                EditText host_description = (EditText) rootView.findViewById(R.id.host_description_et);

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


        return rootView;
    }


}
