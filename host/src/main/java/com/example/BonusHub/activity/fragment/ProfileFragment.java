package com.example.BonusHub.activity.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.executors.GetHostInfoExecutor;
import com.example.BonusHub.activity.executors.UploadHostPhotoExecutor;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;
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

        GetHostInfoExecutor.getInstance().setCallback(new GetHostInfoExecutor.Callback() {
            @Override
            public void onLoaded(Host host) {
                onHostInfoLoaded(host);
            }
        });


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

        return rootView;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        progress.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        host_id = getActivity().getPreferences(MODE_PRIVATE).getInt("host_id", -1);
        progress = ProgressDialog.show(mainActivity, "Загрузка", "Подождите пока загрузится информация о Вас", true);
        GetHostInfoExecutor.getInstance().loadInfo(host_id);
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


    private void onHostInfoLoaded(Host host) {
        Log.d(TAG, "InfoHostSuccessfuly loaded");
        progress.dismiss();
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
            host_open_time_tv.setText(open_hour + ":" + "00");
        if (close_minute != 0)
            host_close_time_tv.setText(close_hour + ":" + close_minute);
        else
            host_close_time_tv.setText(close_hour + ":" + "00");

        // setImage
        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);
        if (imageUriString != null) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver()
                        .openInputStream(Uri.parse(imageUriString)));
                BitmapDrawable bdrawable = new BitmapDrawable(getContext().getResources(), bitmap);
                imgView.setBackground(bdrawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



}
