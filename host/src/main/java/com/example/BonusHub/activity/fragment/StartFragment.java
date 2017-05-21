package com.example.BonusHub.activity.fragment;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.example.BonusHub.activity.AuthUtils;
import com.example.bonuslib.FragmentType;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.api.host.HostResult;
import com.example.BonusHub.activity.api.host.Hoster;
import com.example.BonusHub.activity.executors.DbExecutorService;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.FragmentType;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.BonusHub.activity.api.RetrofitFactory.retrofitBarmen;

public class StartFragment extends Fragment implements NetworkThread.ExecuteCallback <HostResult> {
    private static final String LOGIN_PREFERENCES = "LoginData";

    private int open_hour = 0, open_minute = 0, close_hour = 0, close_minute = 0;
    private Button open_time_btn;
    private Button close_time_btn;
    private EditText host_title;
    private EditText host_description;
    private EditText host_address;
    private static int host_id;
    private LogInActivity logInActivity;
    private ProgressDialog progressDialog;

    View rootView;

    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (progressDialog != null)
            progressDialog.dismiss();
        NetworkThread.getInstance().setCallback(null);
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

        setHasOptionsMenu(true);

        return rootView;
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.continue_btn:

                //hide keyboard
                InputMethodManager imm = (InputMethodManager) logInActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(logInActivity.getCurrentFocus().getWindowToken(), 0);


                String title = host_title.getText().toString();
                String description = host_description.getText().toString();
                String address = host_address.getText().toString();
                if (title.equals(""))
                    host_title.setError("Введите название");
                else if (description.equals(""))
                    host_description.setError("Введите описание");
                else if (address.equals(""))
                    host_address.setError("Введите адрес");
                else {
                    Host host = new Host(title, description, address);
                    host.setTime_open(open_hour * 60 + open_minute);
                    host.setTime_close(close_hour * 60 + close_minute);
                    host.setProfile_image(null);


                    progressDialog = new ProgressDialog(logInActivity);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Отправка информации на сервер...");
                    progressDialog.show();


                    final Hoster hoster = retrofitBarmen().create(Hoster.class);
                    final Call<HostResult> call = hoster.login(host, AuthUtils.getCookie(getActivity()));
                    //final Call<HostResult> call = hoster.login(host, "no");
                    NetworkThread.getInstance().setCallback(this);
                    NetworkThread.getInstance().execute(call);


                    DbExecutorService.getInstance().createHost(host, new DbExecutorService.DbExecutorCallback() {
                        @Override
                        public void onSuccess(Map<String, ?> result) {
                            onHostCreated((Integer) result.get("host_id"));
                        }

                        @Override
                        public void onError(Exception ex) {
                            Toast.makeText(logInActivity, "Произошла ошибка при создании заведения", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    @Override
    public void onResponse(Call<HostResult> call, Response<HostResult> response) {
        progressDialog.dismiss();
    }


    @Override
    public void onFailure(Call<HostResult> call, Response<HostResult> response) {
        progressDialog.dismiss();
        Toast.makeText(getActivity(), response.body().toString(), Toast.LENGTH_SHORT).show();
        AuthUtils.logout(getActivity().getApplicationContext());
        goToLogin();
    }


    @Override
    public void onSuccess(HostResult result) {
        if (result.getCode() == 0) {
            AuthUtils.setHosted(getActivity().getApplicationContext(), true);
            goToMainActivity();
        }
        else
            Toast.makeText(getActivity(), "Ошибка заполнения", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception ex) {
        progressDialog.dismiss();
        Log.d("LoginExeption", ex.getMessage());
    }
}
