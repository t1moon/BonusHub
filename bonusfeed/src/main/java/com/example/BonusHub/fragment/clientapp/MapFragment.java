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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.BonusHub.Location;
import com.example.BonusHub.activity.ClientMainActivity;
import com.example.BonusHub.db.HelperFactory;
import com.example.BonusHub.db.client.Client;
import com.example.BonusHub.db.client_host.ClientHost;
import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.recycler.HostAdapter;
import com.example.BonusHub.retrofit.ClientApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.auth.LoginResponse;
import com.example.BonusHub.retrofit.clientapp.HostListResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.timur.BonusHub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private int host_id = -1;

    private ClientMainActivity mainActivity;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Location location;
    private List<ClientHost> clientHostsList = new ArrayList<>();
    private List<Host> hostsList = new ArrayList<>();

    private NetworkThread.ExecuteCallback<HostListResponse> listHostsCallback;
    private Integer hostsCallbackId;

    private GoogleMap.OnMarkerClickListener markerClickListener;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (ClientMainActivity) getActivity();
        prepareCallbacks();
        prepareListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hostsCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
            hostsCallbackId = null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        mapFragment = (SupportMapFragment) (getChildFragmentManager()
                .findFragmentById(R.id.mini_map));
        mapFragment.getMapAsync(this);
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

    private void setMap() {
        Bundle args = getArguments();
        Host host = null;
        if (args  != null && args.containsKey("host_id")) {
            host_id = args.getInt("host_id", -1);
            try {
                host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if ((host != null) && (map != null)) {
            Location currentLocation = getLocation(host.getAddress());
            if (currentLocation != null) {
                LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongtitude());
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
            }
        }

        for (ClientHost clientHost : clientHostsList) {
            try {
                hostsList.add(HelperFactory.getHelper().getHostDAO().getHostById(clientHost.getHost().getId()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (Host currHost : hostsList) {
            if (currHost != null) {
                String address = currHost.getAddress();
                String title = currHost.getTitle();
                Location currentLocation = getLocation(address);
                if ((map != null) && ((currentLocation != null))) {
                    if (currentLocation != null) {
                        LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongtitude());
                        map.addMarker(new MarkerOptions()
                                .position(pos)
                                .title(title));
                        map.setOnMarkerClickListener(markerClickListener);
                    }
                }
            }
        }

    }

    private Location getLocation(String address) {
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        if (address == null) {
            return null;
        }
        else {
            try {
                addresses = geoCoder.getFromLocationName(address, 3);
                if (addresses.size() > 0) {
                    Location currentLoc = new Location(addresses.get(0).getAddressLine(0),
                            addresses.get(0).getLatitude(),
                            addresses.get(0).getLongitude());
                    return currentLoc;
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    private void getFromCache() {
        clientHostsList.clear();
        Client client = null;
        int client_id = mainActivity.getPreferences(Context.MODE_PRIVATE).
                getInt(ClientMainActivity.CLIENT_ID, -1);
        try {
            client = HelperFactory.getHelper().getClientDAO().getClientById(client_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<ClientHost> clientHosts = new ArrayList<>();
        try {
            clientHosts = HelperFactory.getHelper().getClientHostDAO().lookupHostForClient(client);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        for (ClientHost item : clientHosts) {
            clientHostsList.add(item);
        }

        setMap();
    }


    private void getFromInternet() {
        clientHostsList.clear();
        final ClientApiInterface clientApiInterface = RetrofitFactory.retrofitClient().create(ClientApiInterface.class);
        final Call<HostListResponse> call = clientApiInterface.listHosts(AuthUtils.getCookie(mainActivity));
        if (hostsCallbackId == null) {
            hostsCallbackId = NetworkThread.getInstance().registerCallback(listHostsCallback);
            NetworkThread.getInstance().execute(call, hostsCallbackId);
        }
    }

    private void showResponse(HostListResponse response) {
        // clear tables
        HelperFactory.getHelper().clearTablesForClient(HelperFactory.getHelper().getConnectionSource());
        clientHostsList.clear();

        List<HostListResponse.HostPoints> hostPoints = response.getHosts();
        List<ClientHost> clientHosts = new ArrayList<>();
        ClientHost clientHost = null;
        Client client = null;
        int client_id = mainActivity.getPreferences(Context.MODE_PRIVATE).
                getInt(ClientMainActivity.CLIENT_ID, -1);

        try {
            client = HelperFactory.getHelper().getClientDAO().getClientById(client_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (HostListResponse.HostPoints hp : hostPoints) {
            Host host = new Host(hp.getTitle(), hp.getDescription(), hp.getAddress(), hp.getTime_open(), hp.getTime_close());
            host.setProfile_image(hp.getProfile_image());
            try {
                HelperFactory.getHelper().getHostDAO().createHost(host);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            clientHost = new ClientHost(client, host, hp.getPoints());
            try {
                HelperFactory.getHelper().getClientHostDAO().createClientHost(client, host, hp.getPoints());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            clientHostsList.add(clientHost);
        }

        setMap();
    }

    public void goToHostFragment(int host_id) {
        final Bundle bundle = new Bundle();
        bundle.putInt("host_id", host_id);
        mainActivity.pushFragment(new HostFragment(), true, bundle);
    }

    private void prepareListeners() {
        markerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (Host host:hostsList) {
                    if (marker.getTitle().equals(host.getTitle())) {
                        goToHostFragment(host.getId());
                        return true;
                    }
                }
                return false;
            }
        };
    }

    private void prepareCallbacks() {
        listHostsCallback = new NetworkThread.ExecuteCallback<HostListResponse>() {
            @Override
            public void onResponse(Call<HostListResponse> call, Response<HostListResponse> response) {

            }

            @Override
            public void onFailure(Call<HostListResponse> call, Response<HostListResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
                hostsCallbackId = null;
                if (response.code() == 403) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    AuthUtils.setCookie(getActivity(), "");

                }
                else if(response.code() > 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(HostListResponse result) {
                NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
                hostsCallbackId = null;
                showResponse(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(hostsCallbackId);
                hostsCallbackId = null;
                //showError(ex);
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (mainActivity.hasConnection()) {
            getFromInternet();
        } else {
            getFromCache();
        }
    }
}
