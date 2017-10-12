package com.example.BonusHub.fragment;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.BonusHub.Location;
import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.host.HostResult;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.utils.FragmentType;
import com.example.BonusHub.activity.LogInActivity;
import com.example.BonusHub.executor.DbExecutorService;
import com.example.timur.BonusHub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.BonusHub.retrofit.RetrofitFactory.retrofitHost;

public class StartFragment extends Fragment  implements OnMapReadyCallback {

    private static DbExecutorService.DbExecutorCallback dBHostCallback;
    private static NetworkThread.ExecuteCallback<HostResult> netHostCallback;
    private Integer netHostCallbackId;

    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title;
    private EditText host_description;
    private EditText host_address;
    private static int host_id;
    private LogInActivity logInActivity;
    private ProgressDialog progressDialog;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Location location;

    View rootView;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
        prepareCallbacks();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
        if (netHostCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(netHostCallbackId);
        }
        DbExecutorService.getInstance().setCallback(null);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_start, container, false);

        ImageView backdrop = (ImageView) logInActivity.findViewById(R.id.login_backdrop);
        backdrop.setBackgroundColor(Color.WHITE);
        host_title = (EditText) rootView.findViewById(R.id.host_title_et);
        host_description = (EditText) rootView.findViewById(R.id.host_description_et);
        host_address = (EditText) rootView.findViewById(R.id.host_address_et);
        open_time_btn = (Button) rootView.findViewById(R.id.open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.close_time_btn);

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

        mapFragment = (SupportMapFragment) (getChildFragmentManager()
                .findFragmentById(R.id.mini_map));
        mapFragment.getMapAsync(this);

        host_address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (map != null) {
                        Location currentLocation = getLocation();
                        if (currentLocation != null) {
                            map.clear();
                            LatLng pos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongtitude());
                            map.addMarker(new MarkerOptions()
                                    .position(pos)
                                    .title("Ваше кафе"));
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,17));
                        }
                    }
                }
            }
        });
        setHasOptionsMenu(true);

        return rootView;
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getActivity(), HostMainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void goToLogin() {
        logInActivity.setCurrentFragment(FragmentType.LogInFragment);
        logInActivity.pushFragment(new LogInFragment(), true);
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

                        } else {
                            close_time_btn.setTextSize(20);
                            if (minute != 0)
                                close_time_btn.setText(hourOfDay + ":" + minute);
                            else
                                close_time_btn.setText(hourOfDay + ":" + "00");
                            close_hour = hourOfDay;
                            close_minute = minute;
                        }

                    }
                }, 0, 0, false);
        timePickerDialog.show();
    }

    private Location getLocation() {
        String address = host_address.getText().toString();
        Boolean geoResult = false;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.continue_btn:

                //hide keyboard
                View view = logInActivity.getCurrentFocus();
                if (view!= null) {
                    InputMethodManager imm = (InputMethodManager) logInActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                
                String title = host_title.getText().toString();
                String description = host_description.getText().toString();
                String address = host_address.getText().toString();
                Location hubLocation = getLocation();
                address = hubLocation.getAddress();
                if (title.equals(""))
                    host_title.setError("Введите название");
                else if (description.equals(""))
                    host_description.setError("Введите описание");
                else if (address.equals(""))
                    host_address.setError("Введите адрес");
                else if (hubLocation == null)
                    host_address.setError("Неверный адрес");
                else {
                    Host host = new Host(title, description, address);
                    host.setTime_open(open_hour * 60 + open_minute);
                    host.setTime_close(close_hour * 60 + close_minute);
                    host.setProfile_image(null);


                    progressDialog = new ProgressDialog(logInActivity);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Отправка информации на сервер...");
                    progressDialog.show();


                    final HostApiInterface hostApiInterface = retrofitHost().create(HostApiInterface.class);
                    final Call<HostResult> call = hostApiInterface.createHost(host, AuthUtils.getCookie(getActivity()));
                    if (netHostCallbackId == null) {
                        netHostCallbackId = NetworkThread.getInstance().registerCallback(netHostCallback);
                        NetworkThread.getInstance().execute(call, netHostCallbackId);
                    }
                    DbExecutorService.getInstance().setCallback(dBHostCallback);
                    DbExecutorService.getInstance().createHost(host);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void onHostCreated(int host_id) {
        getActivity().getPreferences(MODE_PRIVATE).edit()
                .putInt("host_id", host_id).apply();
    }




    private void prepareCallbacks() {
        netHostCallback = new NetworkThread.ExecuteCallback <HostResult>() {
            @Override
            public void onResponse(Call<HostResult> call, Response<HostResult> response) {
                progressDialog.dismiss();
            }


            @Override
            public void onFailure(Call<HostResult> call, Response<HostResult> response) {
                progressDialog.dismiss();
                NetworkThread.getInstance().unRegisterCallback(netHostCallbackId);
                netHostCallbackId = null;
                Toast.makeText(getActivity(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                AuthUtils.logout(getActivity().getApplicationContext());
                goToLogin();
            }


            @Override
            public void onSuccess(HostResult result) {
                if (result.getCode() == 0) {
                    AuthUtils.setHosted(getActivity().getApplicationContext(), true);
                    getActivity().getPreferences(MODE_PRIVATE).edit()
                            .putString("host_ident", result.getHostId()).apply();
                    Toast.makeText(getActivity(), result.getHostId(), Toast.LENGTH_SHORT).show();
                    goToMainActivity();
                }
                else
                    Toast.makeText(getActivity(), "Ошибка заполнения", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(netHostCallbackId);
                netHostCallbackId = null;
                progressDialog.dismiss();
                Log.d("LoginExeption", ex.getMessage());
            }
        };

        dBHostCallback = new DbExecutorService.DbExecutorCallback() {
            @Override
            public void onSuccess(Map<String, ?> result) {
                onHostCreated((Integer) result.get("host_id"));
            }

            @Override
            public void onError(Exception ex) {
                Toast.makeText(logInActivity, "Произошла ошибка при создании заведения", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(point)
                        .title("Ваше кафе"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(point,17));
            }
        });
    }

}
