package com.example.BonusHub.activity.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 12500;
    private TextView host_open_time_tv;
    private TextView host_close_time_tv;
    private TextView host_title;
    private TextView host_description;
    private TextView host_address;
    private Button loadImageBtn;
    private int host_id;
    private  String imageUri;
    private MainActivity mainActivity;
    private static int RESULT_LOAD_IMG = 1;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
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
        setInfo();

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

        // Here I can add logo on appbar
//        LinearLayout logoLinearLayout = (LinearLayout)getActivity().findViewById(R.id.logo_layout);
//        CircleImageView logoView = new CircleImageView(getActivity());
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(500, 500);
//        logoView.setLayoutParams(params);
//        logoView.setImageResource(R.drawable.bonus_logo);
//        logoLinearLayout.addView(logoView);
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
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri targetUri = data.getData();
                Bitmap bitmap;
                ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(targetUri));
                BitmapDrawable bdrawable = new BitmapDrawable(getContext().getResources(), bitmap);
                imgView.setBackground(bdrawable);
                updateImage(targetUri.toString());
            } else {
                Toast.makeText(mainActivity, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void updateImage(String targetUri) {
        try {
            UpdateBuilder<Host, Integer> updateBuilder = HelperFactory.getHelper().
                    getHostDAO().updateBuilder();
            updateBuilder.where().eq(Host.HOST_ID_FIELD_NAME, host_id);
            updateBuilder.updateColumnValue(Host.HOST_IMAGE_FIELD_NAME, targetUri);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setInfo() {
        host_id = getActivity().getPreferences(MODE_PRIVATE).getInt("host_id", -1);
        Log.d("host", Integer.toString(host_id));
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

            imageUri = host.getProfile_image();
            if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                return;
            } else {
                showImage();
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImage();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private void showImage() {
        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);
        if(imageUri != null){
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver()
                        .openInputStream(Uri.parse(imageUri)));
                BitmapDrawable bdrawable = new BitmapDrawable(getContext().getResources(), bitmap);
                imgView.setBackground(bdrawable);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
