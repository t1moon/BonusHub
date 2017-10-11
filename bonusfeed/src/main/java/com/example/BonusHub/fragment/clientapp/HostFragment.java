package com.example.BonusHub.fragment.clientapp;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    private int host_id = -1;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Location location;

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

    private void setMap() {
        String address = host_address.getText().toString();
        String title = host_title.getText().toString();
        host_address.setText(address);
        if (map != null) {
                Location currentLocation = getLocation(address);
                if (currentLocation != null) {
                    LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongtitude());
                    map.addMarker(new MarkerOptions()
                            .position(pos)
                            .title(title));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,17));
                }
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

    private Location getLocation(String address) {
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geoCoder.getFromLocationName(address, 3);
            if (addresses.size() > 0) {
                Location currentLoc = new Location(addresses.get(0).getAddressLine(0),
                        addresses.get(0).getLatitude(),
                        addresses.get(0).getLongitude());
                return currentLoc;
            }
            else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMap();
    }
}
