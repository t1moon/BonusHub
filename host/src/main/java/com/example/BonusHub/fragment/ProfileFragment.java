package com.example.BonusHub.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.BonusHub.activity.MainActivity;
import com.example.BonusHub.executors.GetHostInfoExecutor;
import com.example.BonusHub.executors.UploadHostPhotoExecutor;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import java.io.FileNotFoundException;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private final static String TAG = ProfileFragment.class.getSimpleName();

    public static final int UPLOAD_RESULT_OK = 0;
    public static final int UPLOAD_RESULT_FAIL = 1;
    public static final int UPLOAD_RESULT_FILE_NOT_FOUND = 2;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 12500;
    private static int RESULT_LOAD_IMG = 1;
    
    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    private Button loadImageBtn;
    private int host_id;
    private MainActivity mainActivity;



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

        UploadHostPhotoExecutor.getInstance().setCallback(new UploadHostPhotoExecutor.Callback() {
            @Override
            public void onUploaded(int resultCode, BitmapDrawable bdrawable) {
                onHostPhotoUploaded(resultCode, bdrawable);
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
        loadImageBtn = (Button) rootView.findViewById(R.id.buttonLoadPicture);
        final TextView edit = (TextView) rootView.findViewById(R.id.edit);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.pushFragment(new EditFragment(), true);
            }
        });
        loadImageBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });
        return rootView;
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
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                Uri targetUri = data.getData();
                UploadHostPhotoExecutor.getInstance().upload(getContext(), host_id, targetUri);
            } else {
                Toast.makeText(mainActivity, "Вы не выбрали изображение", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Что-то пошло не так", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        host_id = getActivity().getPreferences(MODE_PRIVATE).getInt("host_id", -1);
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


    private void onHostPhotoUploaded(int resultCode, BitmapDrawable bdrawable) {
        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);

        if (resultCode == ProfileFragment.UPLOAD_RESULT_OK) {
            Toast.makeText(mainActivity, "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
            imgView.setBackground(bdrawable);
        }
        if (resultCode == ProfileFragment.UPLOAD_RESULT_FILE_NOT_FOUND) {
            Toast.makeText(mainActivity, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == ProfileFragment.UPLOAD_RESULT_FAIL) {
            Toast.makeText(mainActivity, "Произошла ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
        }
    }

}
