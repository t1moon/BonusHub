package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.executor.DbExecutorService;
import com.example.BonusHub.activity.retrofit.ApiInterface;
import com.example.BonusHub.activity.retrofit.RetrofitFactory;
import com.example.BonusHub.activity.retrofit.editInfo.EditResponse;
import com.example.BonusHub.activity.retrofit.editInfo.UploadResponse;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class EditFragment extends Fragment {
    private static int RESULT_LOAD_IMG = 1;

    private static NetworkThread.ExecuteCallback<EditResponse> editCallback;
    private Integer editCallbackId;
    private static NetworkThread.ExecuteCallback<UploadResponse> uploadCallback;
    private Integer uploadCallbackId;
    private static DbExecutorService.DbExecutorCallback dBUploadCallback;
    private static DbExecutorService.DbExecutorCallback dBInfoCallback;

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
    private MainActivity mainActivity;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
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
        fab_upload = (FloatingActionButton) mainActivity.findViewById(R.id.fab);
        fab_upload.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_a_photo_black_24dp));
        fab_upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImagefromGallery(v);
            }
        });
        setHasOptionsMenu(true);

        getFromCache();

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
            String title = host.getTitle();
            String description = host.getDescription();
            String address = host.getAddress();
            open_hour = host.getTime_open() / 60;
            open_minute = host.getTime_open() % 60;
            close_hour = host.getTime_close() / 60;
            close_minute = host.getTime_close() % 60;

            host_title_et.setText(title);
            host_description_et.setText(description);
            host_address_et.setText(address);

            String open_time = String.format("%02d:%02d", open_hour, open_minute);
            String close_time = String.format("%02d:%02d", close_hour, close_minute);
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
                            String open_time = String.format("%02d:%02d", hourOfDay, minute);
                            open_time_btn.setText(open_time);
                            open_hour = hourOfDay;
                            open_minute = minute;
                        } else {
                            String close_time = String.format("%02d:%02d", hourOfDay, minute);
                            close_time_btn.setText(close_time);
                            close_hour = hourOfDay;
                            close_minute = minute;
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
                InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainActivity.getCurrentFocus().getWindowToken(), 0);

                Host host = new Host();
                host.setTitle(host_title_et.getText().toString());
                host.setDescription(host_description_et.getText().toString());
                host.setAddress(host_address_et.getText().toString());
                host.setTime_open(open_hour * 60 + open_minute);
                host.setTime_close(close_hour * 60 + close_minute);

                final ApiInterface apiInterface = RetrofitFactory.retrofitHost().create(ApiInterface.class);
                Call<EditResponse> call = apiInterface.editHost(host, AuthUtils.getCookie(getActivity().getApplicationContext()));
                if (editCallbackId == null) {
                    editCallbackId = NetworkThread.getInstance().registerCallback(editCallback);
                    NetworkThread.getInstance().execute(call, editCallbackId);
                }
                mainActivity.popFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showError(Throwable error) {
        mainActivity.showSnack("Не удалось обновить информацию");
        new AlertDialog.Builder(getActivity())
                .setTitle("Ошибка")
                .setMessage(error.getMessage())
                .setPositiveButton("OK", null)
                .show();

    }

    private void showResponse(EditResponse result) {
        Toast.makeText(mainActivity, result.getMessage(), Toast.LENGTH_SHORT).show();

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

                String path = getRealPathFromURI(mainActivity, targetUri);
                final ApiInterface apiInterface = RetrofitFactory.retrofitHost().   create(ApiInterface.class);
                File file = new File(path);
                // create RequestBody instance from file
                RequestBody requestFile =  RequestBody.create(MediaType.parse("*/*"), file);

                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

                Call<UploadResponse> call = apiInterface.upload(body, AuthUtils.getCookie(getActivity().getApplicationContext()));

                if (uploadCallbackId == null) {
                    uploadCallbackId = NetworkThread.getInstance().registerCallback(uploadCallback);
                    NetworkThread.getInstance().execute(call, uploadCallbackId);
                }
            } else {
                Toast.makeText(mainActivity, "Вы не выбрали изображение", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Что-то пошло не так", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void onHostPhotoUploaded(String src) {
        ImageView imgView = (ImageView) mainActivity.findViewById(R.id.backdrop);
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
                Toast.makeText(getActivity(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                AuthUtils.logout(getActivity().getApplicationContext());
                goToLogin();
            }

            @Override
            public void onSuccess(UploadResponse result) {
                NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
                uploadCallbackId = null;
                Toast.makeText(mainActivity, result.getMessage(), Toast.LENGTH_SHORT).show();
                DbExecutorService.getInstance().setCallback(dBUploadCallback);
                DbExecutorService.getInstance().upload(getContext(), host_id, targetUri);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
                uploadCallbackId = null;
                NetworkThread.getInstance().unRegisterCallback(uploadCallbackId);
                uploadCallbackId = null;
                Toast.makeText(getActivity(), "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT).show();
                AuthUtils.logout(getActivity().getApplicationContext());
                goToLogin();
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
                showError(ex);
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
                Toast.makeText(mainActivity, "Информацию из кэша загрузить не удалось", Toast.LENGTH_SHORT).show();

            }
        };
    }
}
