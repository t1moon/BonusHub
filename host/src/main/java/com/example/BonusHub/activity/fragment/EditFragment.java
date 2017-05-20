package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.executors.EditHostInfoExecutor;
import com.example.BonusHub.activity.executors.UploadHostPhotoExecutor;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;

import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment {


    public static final int UPLOAD_RESULT_OK = 0;
    public static final int UPLOAD_RESULT_FAIL = 1;
    public static final int UPLOAD_RESULT_FILE_NOT_FOUND = 2;
    private static int RESULT_LOAD_IMG = 1;


    public static final int RESULT_OK = 0;
    public static final int RESULT_FAIL = 1;
    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private boolean set_open_time = false, set_close_time = false;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title_et;
    private EditText host_description_et;
    private EditText host_address_et;
    private FloatingActionButton fab_upload;
    View rootView;
    int host_id;
    MainActivity mainActivity;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        EditHostInfoExecutor.getInstance().setCallback(new EditHostInfoExecutor.Callback() {
            @Override
            public void onEdited(int resultCode) {
                onHostInfoEdited(resultCode);
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
    public void onDestroy(){
        super.onDestroy();
        EditHostInfoExecutor.getInstance().setCallback(null);
        UploadHostPhotoExecutor.getInstance().setCallback(null);
    }

    private void onHostInfoEdited(int resultCode) {
        if (resultCode == RESULT_FAIL) {
            Toast.makeText(mainActivity, "Произошла ошибка при изменении данных", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mainActivity, "Данные были успешно изменены", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        set_open_time = false;
        set_close_time = false;

        host_title_et = (EditText) rootView.findViewById(R.id.edit_host_title_et);
        host_description_et = (EditText) rootView.findViewById(R.id.edit_host_description_et);
        host_address_et = (EditText) rootView.findViewById(R.id.edit_host_address_et);
        open_time_btn = (Button) rootView.findViewById(R.id.edit_open_time_btn);
        close_time_btn = (Button) rootView.findViewById(R.id.edit_close_time_btn);
        fab_upload = (FloatingActionButton) mainActivity.findViewById(R.id.fab);
        fab_upload.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp));
        fab_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });
        setHasOptionsMenu(true);

        setInfo();

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

        return rootView;
    }


    public void setInfo() {
        host_id = getActivity().getPreferences(Context.MODE_PRIVATE).getInt("host_id", -1);
        Log.d("host_Id", Integer.toString(host_id));
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

            host_title_et.setText(title);
            host_description_et.setText(description);
            host_address_et.setText(address);
            if (open_minute != 0)
                open_time_btn.setText(open_hour + ":" + open_minute);
            else
                open_time_btn.setText(open_hour + ":" + "00");
            if (close_minute != 0)
                close_time_btn.setText(close_hour + ":" + close_minute);
            else
                close_time_btn.setText(close_hour + ":" + "00");
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
                            open_time_btn.setTextSize(20);
                            if (minute != 0)
                                open_time_btn.setText(hourOfDay + ":" + minute);
                            else
                                open_time_btn.setText(hourOfDay + ":" + "00");
                            open_hour = hourOfDay;
                            open_minute = minute;
                            set_open_time = true;
                        } else {
                            close_time_btn.setTextSize(20);
                            if (minute != 0)
                                close_time_btn.setText(hourOfDay + ":" + minute);
                            else
                                close_time_btn.setText(hourOfDay + ":" + "00");
                            close_hour = hourOfDay;
                            close_minute = minute;
                            set_close_time = true;
                        }

                    }
                }, 0, 0, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.continue_btn:
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainActivity.getCurrentFocus().getWindowToken(), 0);

                Host host = new Host();
                host.setTitle(host_title_et.getText().toString());
                host.setDescription(host_description_et.getText().toString());
                host.setAddress(host_address_et.getText().toString());
                if (set_open_time)
                    host.setTime_open(open_hour * 60 + open_minute);
                if (set_close_time)
                    host.setTime_close(close_hour * 60 + close_minute);

                EditHostInfoExecutor.getInstance().editInfo(host_id, host);
                mainActivity.popFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            if (requestCode == RESULT_LOAD_IMG  && null != data) {
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

    private void onHostPhotoUploaded(int resultCode, BitmapDrawable bdrawable) {
        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);

        if (resultCode == EditFragment.UPLOAD_RESULT_OK) {
            Toast.makeText(mainActivity, "Изображение успешно загружено", Toast.LENGTH_SHORT).show();
            imgView.setBackground(bdrawable);
        }
        if (resultCode == EditFragment.UPLOAD_RESULT_FILE_NOT_FOUND) {
            Toast.makeText(mainActivity, "Файл не найден", Toast.LENGTH_SHORT).show();
        }
        if (resultCode == EditFragment.UPLOAD_RESULT_FAIL) {
            Toast.makeText(mainActivity, "Произошла ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
        }
    }


}
