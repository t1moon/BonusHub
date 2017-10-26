package com.example.BonusHub.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.activity.LogInActivity;
import com.example.BonusHub.db.HelperFactory;
import com.example.BonusHub.db.host.Host;
import com.example.BonusHub.executor.DbExecutorService;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private final static String TAG = ProfileFragment.class.getSimpleName();

    private static NetworkThread.ExecuteCallback<GetInfoResponse> netInfoCallback;
    private Integer netInfoCallbackId;
    private static DbExecutorService.DbExecutorCallback dBHostCallback;
    private static DbExecutorService.DbExecutorCallback dBInfoCallback;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 12500;
    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    FloatingActionButton fab_edit;
    private int host_id;
    private String identificator;
    private HostMainActivity hostMainActivity;
    ProgressDialog progressDialog;
    String pathToImageProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareCallbacks();
        hostMainActivity = (HostMainActivity) getActivity();
        if (ContextCompat.checkSelfPermission(hostMainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(hostMainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (netInfoCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
        }
        DbExecutorService.getInstance().setCallback(null);
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
        fab_edit = (FloatingActionButton) hostMainActivity.findViewById(R.id.fab);
        fab_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        fab_edit.setVisibility(View.VISIBLE);

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostMainActivity.pushFragment(new EditFragment(), true);
            }
        });

        host_id = getActivity().getPreferences(MODE_PRIVATE).getInt("host_id", -1);

        if(hostMainActivity.hasConnection())
            getFromInternet();
        else
            getFromCache();

        return rootView;
    }

    private void getFromCache() {
        DbExecutorService.getInstance().setCallback(dBInfoCallback);
        DbExecutorService.getInstance().loadInfo(host_id);
    }

    private void getFromInternet() {
        identificator = AuthUtils.getHostId(hostMainActivity.getApplicationContext());
        progressDialog = ProgressDialog.show(hostMainActivity, "Загрузка", "Подождите пока загрузится информация о Вас", true);
        final HostApiInterface hostApiInterface = RetrofitFactory.retrofitHost().create(HostApiInterface.class);
        final Call<GetInfoResponse> call = hostApiInterface.getInfo(AuthUtils.getCookie(hostMainActivity.getApplicationContext()));
        if (netInfoCallbackId == null) {
            netInfoCallbackId = NetworkThread.getInstance().registerCallback(netInfoCallback);
            NetworkThread.getInstance().execute(call, netInfoCallbackId);
        }
    }

    private void goToLogIn() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showError(Exception error) {
        progressDialog.dismiss();
        new AlertDialog.Builder(hostMainActivity)
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }

    private void showResponse(GetInfoResponse result) {
        progressDialog.dismiss();
        getActivity().getPreferences(MODE_PRIVATE).edit()
                .putInt("loy_type", result.getLoyalityType()).apply();
        // clear tables
        HelperFactory.getHelper().clearHostTable(HelperFactory.getHelper().getConnectionSource());

        Host host = new Host(
                result.getTitle(),
                result.getDescription(),
                result.getAddress(),
                result.getTime_open(),
                result.getTime_close());

        ImageView imgView = (ImageView) getActivity().findViewById(R.id.backdrop);

        pathToImageProfile = RetrofitFactory.retrofitHost().baseUrl() + RetrofitFactory.MEDIA_URL + result.getProfile_image();
        Glide
                .with(getActivity().getApplicationContext())
                .load(pathToImageProfile)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imgView);

        host.setProfile_image(pathToImageProfile);
        // TO-DO: Выяснить как кешируется glide
//        UploadHostPhotoExecutor.getInstance().upload(getContext(), host_id, targetUri);
        DbExecutorService.getInstance().setCallback(dBHostCallback);
        DbExecutorService.getInstance().createHost(host);
    }

    private void onCacheLoaded(Host host) {
        String title = host.getTitle();
        String description = host.getDescription();
        String address = host.getAddress();
        //int open_hour = host.getTime_open() / 60;
        //int open_minute = host.getTime_open() % 60;
        //int close_hour = host.getTime_close() / 60;
        //int close_minute = host.getTime_close() % 60;
        String open_time = host.getTime_open();
        String close_time = host.getTime_close();
        String imageUriString = host.getProfile_image();
        host_title.setText(title);
        host_description.setText(description);
        host_address.setText(address);
        host_open_time_tv.setText(open_time);
        host_close_time_tv.setText(close_time);

        ImageView imgView = (ImageView) hostMainActivity.findViewById(R.id.backdrop);
        Glide
                .with(getActivity().getApplicationContext())
                .load(imageUriString)
                .fitCenter()
                .into(imgView);
        Toast.makeText(hostMainActivity, "Информация загружена из кэша", Toast.LENGTH_SHORT).show();
    }

    private void onHostCreated(int host_id) {
        Host host = null;
        try {
            host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getActivity().getPreferences(MODE_PRIVATE).edit()
                .putInt("host_id", host_id).apply();
        String title = host.getTitle();
        String description = host.getDescription();
        String address = host.getAddress();
        //int open_hour = host.getTime_open() / 60;
        //int open_minute = host.getTime_open() % 60;
        //int close_hour = host.getTime_close() / 60;
        //int close_minute = host.getTime_close() % 60;
        String imageUriString = host.getProfile_image();
        host_title.setText(title);
        host_description.setText(description);
        host_address.setText(address);

        String open_time = host.getTime_open();
        String close_time = host.getTime_close();
        host_open_time_tv.setText(open_time);
        host_close_time_tv.setText(close_time);

        ImageView imgView = (ImageView) hostMainActivity.findViewById(R.id.backdrop);
        Glide
                .with(getActivity().getApplicationContext())
                .load(imageUriString)
                .fitCenter()
                .into(imgView);

        hostMainActivity.showSnack(true);   // Say to user that info is up-to-date

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "READ_EXTERNAL_STORAGE permission granted");
                } else {
                }
            }
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), LogInActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void prepareCallbacks() {
        netInfoCallback = new NetworkThread.ExecuteCallback<GetInfoResponse>() {
            @Override
            public void onResponse(Call<GetInfoResponse> call, Response<GetInfoResponse> response) {

            }

            @Override
            public void onFailure(Call<GetInfoResponse> call, Response<GetInfoResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
                netInfoCallbackId = null;
                progressDialog.dismiss();
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Вы не являетесь сотрудником или владельцем какого-либо заведения", Toast.LENGTH_SHORT).show();
                }
                if (response.code() == 401) {
                    Toast.makeText(getActivity(), "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(getActivity());
                    goToLogin();

                }
                if (response.code() == 404) {
                    Toast.makeText(getActivity(), "Данное заведение не существует", Toast.LENGTH_SHORT).show();

                }
                else if(response.code() > 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(GetInfoResponse result) {
                NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
                netInfoCallbackId = null;
                showResponse(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(netInfoCallbackId);
                netInfoCallbackId = null;
                showError(ex);
            }
        };

        dBHostCallback = new DbExecutorService.DbExecutorCallback() {
            @Override
            public void onSuccess(Map<String, ?> result) {
                onHostCreated((Integer) result.get("host_id"));
            }

            @Override
            public void onError(Exception ex) {
                new AlertDialog.Builder(hostMainActivity)
                        .setTitle("Ошибка при записи в кэш")
                        .setMessage(ex.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
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
}
