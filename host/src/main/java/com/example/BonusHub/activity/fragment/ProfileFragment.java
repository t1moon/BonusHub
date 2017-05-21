package com.example.BonusHub.activity.fragment;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.executors.DbExecutorService;
import com.example.BonusHub.activity.retrofit.ApiInterface;
import com.example.BonusHub.activity.retrofit.NetworkThread;
import com.example.BonusHub.activity.retrofit.RetrofitFactory;
import com.example.BonusHub.activity.retrofit.getInfo.GetInfoResponse;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private final static String TAG = ProfileFragment.class.getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 12500;
    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    FloatingActionButton fab_edit;
    private int host_id;
    private MainActivity mainActivity;
    ProgressDialog progress;
    String pathToImageProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
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
        fab_edit = (FloatingActionButton) mainActivity.findViewById(R.id.fab);
        fab_edit.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
        fab_edit.setVisibility(View.VISIBLE);

        fab_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.pushFragment(new EditFragment(), true);
            }
        });

        host_id = getActivity().getPreferences(MODE_PRIVATE).getInt("host_id", -1);

        if(mainActivity.hasConnection())
            getFromInternet();
        else
            getFromCache();

        return rootView;
    }

    private void getFromCache() {
        DbExecutorService.getInstance().loadInfo(host_id, new DbExecutorService.DbExecutorCallback() {
            @Override
            public void onSuccess(Map<String, ?> result) {
                onCacheLoaded((Host) result.get("host"));
            }
            @Override
            public void onError(Exception ex) {
                Toast.makeText(mainActivity, "Информацию из кэша загрузить не удалось", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getFromInternet() {
        progress = ProgressDialog.show(mainActivity, "Загрузка", "Подождите пока загрузится информация о Вас", true);

        final ApiInterface apiInterface = RetrofitFactory.retrofitHost().create(ApiInterface.class);
        final Call<GetInfoResponse> call = apiInterface.getInfo(AuthUtils.getCookie(mainActivity));
        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<GetInfoResponse>() {
            @Override
            public void onSuccess(GetInfoResponse result) {
                showResponse(result);
            }

            @Override
            public void onError(Exception ex) {
                showError(ex);
            }
        });
    }

    private void showResponse(GetInfoResponse result) {
        progress.dismiss();
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
        DbExecutorService.getInstance().createHost(host, new DbExecutorService.DbExecutorCallback() {
            @Override
            public void onSuccess(Map<String, ?> result) {
                onHostCreated((Integer) result.get("host_id"));
            }

            @Override
            public void onError(Exception ex) {
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Ошибка при записи в кэш")
                        .setMessage(ex.getMessage())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

    private void showError(Exception error) {
        progress.dismiss();
        new AlertDialog.Builder(mainActivity)
                .setTitle("Ошибка при подключении к серверу")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }

    private void onCacheLoaded(Host host) {
        String title = host.getTitle();
        String description = host.getDescription();
        String address = host.getAddress();
        int open_hour = host.getTime_open() / 60;
        int open_minute = host.getTime_open() % 60;
        int close_hour = host.getTime_close() / 60;
        int close_minute = host.getTime_close() % 60;
        String imageUriString = host.getProfile_image();
        host_title.setText(title);
        host_description.setText(description);
        host_address.setText(address);
        if (open_minute != 0)
            host_open_time_tv.setText(open_hour + ":" + open_minute);
        else
            host_open_time_tv.setText(open_hour + "0:" + "00");
        if (close_minute != 0)
            host_close_time_tv.setText(close_hour + ":" + close_minute);
        else
            host_close_time_tv.setText(close_hour + "0:" + "00");

        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);
        Glide
                .with(getActivity().getApplicationContext())
                .load(imageUriString)
                .fitCenter()
                .into(imgView);
        Toast.makeText(mainActivity, "Информация загружена из кэша", Toast.LENGTH_SHORT).show();
    }

    private void onHostCreated(int host_id) {
        Host host = null;
        try {
            host = HelperFactory.getHelper().getHostDAO().getHostById(host_id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String title = host.getTitle();
        String description = host.getDescription();
        String address = host.getAddress();
        int open_hour = host.getTime_open() / 60;
        int open_minute = host.getTime_open() % 60;
        int close_hour = host.getTime_close() / 60;
        int close_minute = host.getTime_close() % 60;
        String imageUriString = host.getProfile_image();
        host_title.setText(title);
        host_description.setText(description);
        host_address.setText(address);

        String open_time = String.format("%02d:%02d", open_hour, open_minute);
        String close_time = String.format("%02d:%02d", close_hour, close_minute);
        host_open_time_tv.setText(open_time);
        host_close_time_tv.setText(close_time);

        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);
        Glide
                .with(getActivity().getApplicationContext())
                .load(imageUriString)
                .fitCenter()
                .into(imgView);

        mainActivity.showSnack(true);   // Say to user that info is up-to-date

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


}
