package com.techpark.client.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techpark.bonuslib.db.HelperFactory;
import com.techpark.BonusHub.activity.db.host.Host;
import com.techpark.client.R;
import com.techpark.client.retrofit.RetrofitFactory;

import java.sql.SQLException;

public class HostFragment extends Fragment {

    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    private int host_id = -1;

    public HostFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_host, container, false);


        host_title = (TextView) rootView.findViewById(R.id.host_title_tv);
        host_description = (TextView) rootView.findViewById(R.id.host_description_tv);
        host_address = (TextView) rootView.findViewById(R.id.host_address_tv);
        host_open_time_tv = (TextView) rootView.findViewById(R.id.host_open_time_tv);
        host_close_time_tv = (TextView) rootView.findViewById(R.id.host_close_time_tv);

        setInfo();

        return rootView;
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
        Bundle args = getArguments();
        if (args  != null && args.containsKey("host_id"))
            host_id = args.getInt("host_id", -1);

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

            ImageView imgView = (ImageView) getActivity().findViewById(R.id.backdrop);
            String pathToImageProfile = RetrofitFactory.retrofitClient().baseUrl() + RetrofitFactory.MEDIA_URL + host.getProfile_image();
            Glide
                    .with(getActivity().getApplicationContext())
                    .load(pathToImageProfile)
                    .fitCenter()
                    .into(imgView);
        }
    }
}
