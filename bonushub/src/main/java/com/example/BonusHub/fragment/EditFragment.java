package com.example.BonusHub.fragment;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
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

import com.bumptech.glide.Glide;
import com.example.BonusHub.Location;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.activity.LogInActivity;
import com.example.BonusHub.executor.DbExecutorService;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.editInfo.EditResponse;
import com.example.BonusHub.retrofit.editInfo.UploadResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.db.host.Host;
import com.example.timur.BonusHub.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class EditFragment extends Fragment implements OnMapReadyCallback {
    private static int RESULT_LOAD_IMG = 1;

    private static NetworkThread.ExecuteCallback<EditResponse> editCallback;
    private Integer editCallbackId;
    private static NetworkThread.ExecuteCallback<UploadResponse> uploadCallback;
    private Integer uploadCallbackId;
    private static DbExecutorService.DbExecutorCallback dBUploadCallback;
    private static DbExecutorService.DbExecutorCallback dBInfoCallback;

    //private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private String open_time = "00:00", close_time = "00:00";
    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title_et;
    private EditText host_description_et;
    private EditText host_address_et;
    private FloatingActionButton fab_upload;
    private View rootView;
    private int host_id;
    private Uri targetUri;
    private HostMainActivity hostMainActivity;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Location location;
    private Host host;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hostMainActivity = (HostMainActivity) getActivity();
        prepareCallbacks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (editCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(editCallbackId);
        }
        if (uploadCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
        }
        DbExecutorService.getInstance().setCallback(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        host_title_et = (EditText) rootView.findViewById(R.id.edit_host_title_et);
        host_description_et = (EditText) rootView.findViewById(R.id.edit_host_description_et);
        host_address_et = (EditText) rootView.findViewById(R.id.edit_host_address_et);
        open_time_btn = (Button) rootView.findViewById(R.id.edit_open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.edit_close_time_btn);
        fab_upload = (FloatingActionButton) hostMainActivity.findViewById(R.id.fab);
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_a_photo_black_24dp));
        fab_upload.setImageDrawable(drawable);
        fab_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });
        setHasOptionsMenu(true);

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
        getFromCache();
        mapFragment.getMapAsync(this);
        host_address_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,15));
                        }
                    }
                }
            }
        });
        return rootView;
    }

    private Location getLocation() {
        String address = host_address_et.getText().toString();
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

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void getFromCache() {
        Log.d("host","" + host_id);
        host_id = getActivity().getPreferences(MODE_PRIVATE).getInt("host_id", -1);
        DbExecutorService.getInstance().setCallback(dBInfoCallback);
        DbExecutorService.getInstance().loadInfo(host_id);
    }


    public void onCacheLoaded(Host host) {
        if (host != null) {
            this.host = host;
            String title = host.getTitle();
            String description = host.getDescription();
            String address = host.getAddress();
            if (map != null) {
                map.clear();
                LatLng pos = new LatLng(host.getLatitude(), host.getLongitude());
                map.addMarker(new MarkerOptions()
                        .position(pos)
                        .title("Ваше кафе"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,15));
            }
            //open_hour = host.getTime_open() / 60;
            //open_minute = host.getTime_open() % 60;
            //close_hour = host.getTime_close() / 60;
            //close_minute = host.getTime_close() % 60;

            host_title_et.setText(title);
            host_description_et.setText(description);
            host_address_et.setText(address);

            open_time = host.getTime_open();
            close_time = host.getTime_close();
            open_time_btn.setText(open_time);
            close_time_btn.setText(close_time);


        }
    }

    private void pickTime(final View v) {
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (v == open_time_btn) {
                            open_time = String.format("%02d:%02d", hourOfDay, minute);
                            open_time_btn.setText(open_time);
                            //open_hour = hourOfDay;
                            //open_minute = minute;
                        } else {
                            close_time = String.format("%02d:%02d", hourOfDay, minute);
                            close_time_btn.setText(close_time);
                            //close_hour = hourOfDay;
                            //close_minute = minute;
                        }
                    }
                }, 0, 0, true);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.continue_btn:
                //hide keyboard
                View view = hostMainActivity.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) hostMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(hostMainActivity.getCurrentFocus().getWindowToken(), 0);
                String title = host_title_et.getText().toString();
                String description = host_description_et.getText().toString();
                String address = host_address_et.getText().toString();
                Location hubLocation = getLocation();
                if (hubLocation != null) {
                    address = hubLocation.getAddress();
                }
                if (!hostMainActivity.hasConnection()) {
                    Snackbar.make(view, "Нет соединения с сетью", Snackbar.LENGTH_LONG).show();
                }
                else {
                    if (title.equals(""))
                        host_title_et.setError("Введите название");
                    else if (description.equals(""))
                        host_title_et.setError("Введите описание");
                    else if (address.equals("") || (address == null))
                        host_title_et.setError("Введите адрес");
                    else if (hubLocation == null)
                        host_title_et.setError("Неверный адрес");
                    else {
                        Host host = new Host(title, description, address);
                        host.setTime_open(open_time);
                        host.setTime_close(close_time);
                        host.setProfile_image(null);
                        host.setLongitude(hubLocation.getLongtitude());
                        host.setLatitude(hubLocation.getLatitude());

                        final HostApiInterface hostApiInterface = RetrofitFactory.retrofitHost().create(HostApiInterface.class);
                        Call<EditResponse> call = hostApiInterface.editHost(host, AuthUtils.getCookie(getActivity().getApplicationContext()));
                        if (editCallbackId == null) {
                            editCallbackId = NetworkThread.getInstance().registerCallback(editCallback);
                            NetworkThread.getInstance().execute(call, editCallbackId);
                        }
                        hostMainActivity.popFragment();
                        return true;
                    }
                }
            }
        return super.onOptionsItemSelected(item);
    }

    private void showError(Throwable error) {
        hostMainActivity.showSnack("Не удалось обновить информацию");
        new AlertDialog.Builder(getActivity())
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }

    private void showResponse(EditResponse result) {
        Toast.makeText(hostMainActivity, result.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.start_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && null != data) {
                // Get the Image from data
                targetUri = data.getData();

                String path = getRealPathFromURI(hostMainActivity, targetUri);
                final HostApiInterface hostApiInterface = RetrofitFactory.retrofitHost().   create(HostApiInterface.class);
                File file = new File(path);
                // create RequestBody instance from file
                RequestBody requestFile =  RequestBody.create(MediaType.parse("*/*"), file);

                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

                Call<UploadResponse> call = hostApiInterface.upload(body, AuthUtils.getCookie(getActivity().getApplicationContext()));

                if (uploadCallbackId == null) {
                    uploadCallbackId = NetworkThread.getInstance().registerCallback(uploadCallback);
                    NetworkThread.getInstance().execute(call, uploadCallbackId);
                }
            } else {
                Toast.makeText(hostMainActivity, "Вы не выбрали изображение", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(hostMainActivity, "Что-то пошло не так", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void onHostPhotoUploaded(String src) {
        ImageView imgView = (ImageView) hostMainActivity.findViewById(R.id.backdrop);
        Glide
                .with(getActivity().getApplicationContext())
                .load(src)
                .fitCenter()
                .into(imgView);
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void prepareCallbacks() {
        uploadCallback = new NetworkThread.ExecuteCallback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {

            }

            @Override
            public void onFailure(Call<UploadResponse> call, Response<UploadResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
                uploadCallbackId = null;
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Укажите название заведения", Toast.LENGTH_SHORT).show();

                }
                if (response.code() == 401) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    goToLogin();

                }
                if (response.code() == 403) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    goToLogin();

                }
                else if(response.code() > 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(UploadResponse result) {
                NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
                uploadCallbackId = null;
                Toast.makeText(hostMainActivity, result.getMessage(), Toast.LENGTH_SHORT).show();
                DbExecutorService.getInstance().setCallback(dBUploadCallback);
                DbExecutorService.getInstance().upload(getContext(), host_id, targetUri);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
                uploadCallbackId = null;
                new AlertDialog.Builder(getActivity())
                        .setTitle("Упс!")
                        .setMessage("Ошибка соединения с сервером. Проверьте интернет подключение.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        };

        editCallback = new NetworkThread.ExecuteCallback<EditResponse>() {
            @Override
            public void onResponse(Call<EditResponse> call, Response<EditResponse> response) {
            }


            @Override
            public void onFailure(Call<EditResponse> call, Response<EditResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(editCallbackId);
                editCallbackId = null;
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Укажите название заведения", Toast.LENGTH_SHORT).show();

                }
                if (response.code() == 401) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    goToLogin();

                }
                if (response.code() == 403) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    goToLogin();

                }
                else if(response.code() > 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onSuccess(EditResponse result) {
                NetworkThread.getInstance().unRegisterCallback(editCallbackId);
                editCallbackId = null;
                showResponse(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(editCallbackId);
                editCallbackId = null;
                new AlertDialog.Builder(getActivity())
                        .setTitle("Упс!")
                        .setMessage("Ошибка соединения с сервером. Проверьте интернет подключение.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        };

        dBUploadCallback = new DbExecutorService.DbExecutorCallback() {
            @Override
            public void onSuccess(Map<String, ?> result) {
                onHostPhotoUploaded((String) result.get("image"));
            }
            @Override
            public void onError(Exception ex) {
                showError(ex);
            }
        };

        dBInfoCallback = new DbExecutorService.DbExecutorCallback() {
            @Override
            public void onSuccess(Map<String, ?> result) {
                onCacheLoaded((Host) result.get("host"));
            }

            @Override
            public void onError(Exception ex) {
                Toast.makeText(hostMainActivity, "Информацию из кэша загрузить не удалось", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void setMap() {
        String address = host_address_et.getText().toString();
        String title = host_title_et.getText().toString();
        host_address_et.setText(address);
        if ((map != null) && (host != null)) {
            LatLng pos;
            pos = new LatLng(host.getLatitude(), host.getLongitude());
            map.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(title));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,17));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        setMap();
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
