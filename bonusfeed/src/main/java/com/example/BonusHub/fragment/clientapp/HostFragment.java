package com.example.BonusHub.fragment.clientapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.BonusHub.Location;
import com.example.BonusHub.activity.ClientMainActivity;
import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.db.HelperFactory;
import com.example.timur.BonusHub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class HostFragment extends Fragment implements OnMapReadyCallback {

    private ClientMainActivity mainActivity;

    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    private TextView host_loy_descr;
    private int host_id = -1;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Host host;

    public HostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (ClientMainActivity) getActivity();
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
        host_loy_descr = (TextView) rootView.findViewById(R.id.host_loy_descr);

        mapFragment = (SupportMapFragment) (getChildFragmentManager()
                .findFragmentById(R.id.mini_map));
        mapFragment.getMapAsync(this);

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
        try {
            host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (host != null) {
            String title = host.getTitle();
            String description = host.getDescription();
            String address = host.getAddress();
            int loyality_type = host.getLoyalityType();
            float param = host.getLoyalityParam();
            String loyality_descr = "";

            host_title.setText(title);
            host_description.setText(description);
            host_address.setText(address);
            host_open_time_tv.setText(host.getTime_open());
            host_close_time_tv.setText(host.getTime_close());

            if (loyality_type == 1) {
                loyality_descr = getResources().getString(R.string.bonus_feed_description) + Float.toString(param);
            }
            else {
                loyality_descr = getResources().getString(R.string.cup_feed_description) + Integer.toString(Math.round(param));
            }
            host_loy_descr.setText(loyality_descr);

            ImageView imgView = (ImageView) getActivity().findViewById(R.id.backdrop);
            String pathToImageProfile = RetrofitFactory.retrofitClient().baseUrl() + RetrofitFactory.MEDIA_URL + host.getProfile_image();
            Glide
                    .with(getActivity().getApplicationContext())
                    .load(pathToImageProfile)
                    .fitCenter()
                    .into(imgView);
        }
    }

    private void setMap() {
        String address = host_address.getText().toString();
        String title = host_title.getText().toString();
        host_address.setText(address);
        if (map != null) {
            Log.d("Longitude", Double.toString(host.getLongitude()));
            LatLng pos = new LatLng(host.getLatitude(), host.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(title));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,17));
        }
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                goToMapFragment();
            }
        });
    }

    private void goToMapFragment() {
        final Bundle bundle = new Bundle();
        bundle.putInt("host_id", host_id);
        mainActivity.pushFragment(new MapFragment(), true, bundle);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMap();
    }
}
