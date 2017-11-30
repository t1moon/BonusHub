package com.techpark.BonusHub.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techpark.BonusHub.activity.ClientMainActivity;
import com.techpark.BonusHub.activity.LogInActivity;
import com.techpark.BonusHub.retrofit.CommonApiInterface;
import com.techpark.BonusHub.retrofit.RetrofitFactory;
import com.techpark.BonusHub.utils.AuthUtils;
import com.techpark.BonusHub.retrofit.auth.Login;
import com.techpark.BonusHub.retrofit.auth.LoginResponse;
import com.techpark.BonusHub.retrofit.registration.RegistrationResult;
import com.techpark.BonusHub.threadManager.NetworkThread;
import com.techpark.timur.BonusHub.R;

import retrofit2.Call;
import retrofit2.Response;

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

    public void goToMainActivity() {
        Intent intent = new Intent(logInActivity, ClientMainActivity.class);
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

        final CommonApiInterface commonApiInterface = RetrofitFactory.retrofitClient().create(CommonApiInterface.class);
        Call<RegistrationResult> call = commonApiInterface.registrate(new Login(login,password));
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

        final CommonApiInterface commonApiInterface = RetrofitFactory.retrofitClient().create(CommonApiInterface.class);
        Call<LoginResponse> call = commonApiInterface.login(new Login(login,password));

        if (loginCallbackId == null && call != null) {
            loginCallbackId = NetworkThread.getInstance().registerCallback(loginCallback);
            NetworkThread.getInstance().execute(call, loginCallbackId);
        }
    }

    public void onLoginResult(LoginResponse result) {

        if (result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity().getApplicationContext());
            AuthUtils.setHosted(getActivity().getApplicationContext(), true);
            AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
            goToMainActivity();
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

    private void showError(Throwable error) {
        new AlertDialog.Builder(logInActivity)
                .setTitle("Упс!")
                .setMessage("Ошибка соединения с сервером. Проверьте интернет подключение.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void prepareCallbacks() {
        registrationCallback = new NetworkThread.ExecuteCallback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                progressDialog.dismiss();
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
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
                else if(response.code() >= 500) {
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
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                Toast.makeText(getActivity(), "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }
        };

        loginCallback = new NetworkThread.ExecuteCallback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Response<LoginResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
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
                showError(ex);
                //Toast.makeText(getActivity(), "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
