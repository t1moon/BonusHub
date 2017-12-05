package com.example.BonusHub.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BonusHub.activity.HostMainActivity;
import com.example.BonusHub.activity.StaffMainActivity;
import com.example.BonusHub.retrofit.CommonApiInterface;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.RetrofitFactory;
import com.example.BonusHub.retrofit.auth.LoginResponse;
import com.example.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.retrofit.auth.Login;
import com.example.BonusHub.activity.LogInActivity;
import com.example.BonusHub.retrofit.registration.RegistrationResult;
import com.example.BonusHub.utils.FragmentType;
import com.example.timur.BonusHub.R;

import retrofit2.Call;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.BonusHub.retrofit.RetrofitFactory.retrofitCommon;
//import static com.example.BonusHub.retrofit.RetrofitFactory.retrofitHost;

public class RegisterFragment extends Fragment {
    private LogInActivity logInActivity;


    private static NetworkThread.ExecuteCallback<RegistrationResult> registrationCallback;
    private Integer registrationCallbackId;
    private static NetworkThread.ExecuteCallback<LoginResponse> loginCallback;
    private Integer loginCallbackId;

    private Button registrationButton;
    private EditText loginInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;
    View rootView;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
        prepareCallbacks();
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (loginCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
        }
        if (registrationCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
        }
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register, container, false);

        loginInput = (EditText) rootView.findViewById(R.id.login_input);
        passwordInput = (EditText) rootView.findViewById(R.id.password_input);
        registrationButton = (Button) rootView.findViewById(R.id.btn_register);
        registrationButton.setOnClickListener(onRegistrationClickListener);
        setHasOptionsMenu(true);

        return rootView;
    }


    public void goToStartFragment() {
        logInActivity.setCurrentFragment(FragmentType.StartHost);
        logInActivity.pushFragment(new StartFragment(), true);
    }

    public void goToMainActivity() {
        Intent intent = null;
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                intent = new Intent(logInActivity, HostMainActivity.class);
                break;
            case "Staff":
                intent = new Intent(logInActivity, StaffMainActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            logInActivity.finish();
        }
    }

    private final View.OnClickListener onRegistrationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            registrate();
        }
    };

    private void registrate() {
        final String login = loginInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!validate())
            return;

        progressDialog = new ProgressDialog(logInActivity);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Регистрация...");
        progressDialog.show();

        Call<RegistrationResult> call = null;
        final CommonApiInterface commonApiInterface = retrofitCommon().create(CommonApiInterface.class);
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                call = commonApiInterface.registrate(new Login(login,password));
                break;
            case "Staff":
                call = commonApiInterface.registrate(new Login(login,password));
                break;
        }
        if (registrationCallbackId == null && call != null) {
            registrationCallbackId = NetworkThread.getInstance().registerCallback(registrationCallback);
            NetworkThread.getInstance().execute(call, registrationCallbackId);        }

    }

    public void onRegistrationResult(RegistrationResult result) {
        progressDialog.dismiss();
        if (result.getCode() == 0) {
            logIn();
        }
    }

    private void logIn() {
        final String login = loginInput.getText().toString();
        final String password = passwordInput.getText().toString();

        Call<LoginResponse> call = null;
        final CommonApiInterface commonApiInterface = retrofitCommon().create(CommonApiInterface.class);
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                call = commonApiInterface.login(new Login(login,password));
                break;
            case "Staff":
                call = commonApiInterface.login(new Login(login,password));
                break;
        }
        if (loginCallbackId == null && call != null) {
            loginCallbackId = NetworkThread.getInstance().registerCallback(loginCallback);
            NetworkThread.getInstance().execute(call, loginCallbackId);
        }
    }

    public void goToQr() {
        logInActivity.pushFragment(new QRFragment(), true);
    }

    public void onLoginResult(LoginResponse result) {
        AuthUtils.setLogin(getActivity().getApplicationContext(), loginInput.getText().toString());
        //Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();

        if (result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity().getApplicationContext());
            if (!AuthUtils.getRole(logInActivity).equals("Host")) {
                AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
                goToQr();
            } else {
                if (result.getHostId() == null) {
                    AuthUtils.setHosted(getActivity().getApplicationContext(), false);
                    goToStartFragment();
                    AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
                }
                else {
                    AuthUtils.setHosted(getActivity().getApplicationContext(), true);
                    AuthUtils.setHostId(getActivity().getApplicationContext(), result.getHostId());
                    AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
                    goToMainActivity();
                }
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        final String login = loginInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (login.isEmpty()) {
            loginInput.setError("Введите логин");
            valid = false;
        }

        if (password.isEmpty() || password.length() <= 5 ) {
            passwordInput.setError("Не менее 6 символов");
            valid = false;
        }
        return valid;
    }

    private void prepareCallbacks() {

        registrationCallback = new NetworkThread.ExecuteCallback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                progressDialog.dismiss();
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Не указан логин или пароль", Toast.LENGTH_SHORT).show();
                }
                if (response.code() == 409) {
                    Toast.makeText(getActivity(), "Пользователь с таким логином уже существует", Toast.LENGTH_SHORT).show();
                }
                else if(response.code() > 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(RegistrationResult result) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                progressDialog.dismiss();
                if (result.getCode() == 0){
                    onRegistrationResult(result);
                } else if (result.getCode() == 1) {
                    Toast.makeText(getActivity(), "Данный аккаунт уже существует", Toast.LENGTH_SHORT).show();
                    onRegistrationResult(result);
                }


            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }
        };

        loginCallback = new NetworkThread.ExecuteCallback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity().getApplicationContext(), cookie);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Response<LoginResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                if(response.code() >= 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onSuccess(LoginResponse result) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                onLoginResult(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
