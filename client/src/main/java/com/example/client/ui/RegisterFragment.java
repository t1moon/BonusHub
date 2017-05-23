package com.example.client.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.client.AuthUtils;
import com.example.client.retrofit.Login;
import com.example.client.retrofit.login.LoginResult;
import com.example.client.retrofit.login.Loginner;
import com.example.client.retrofit.registration.RegistrationResult;
import com.example.client.retrofit.registration.Registrator;
import com.example.client.threadManager.NetworkThread;
import com.example.bonuslib.FragmentType;
import com.example.client.R;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.client.api.RetrofitFactory.retrofitClient;

/**
 * Created by mike on 05.05.17.
 */

public class RegisterFragment extends Fragment {
    private LogInActivity logInActivity;

    private static NetworkThread.ExecuteCallback<RegistrationResult> registrationCallback;
    private Integer registrationCallbackId;
    private static NetworkThread.ExecuteCallback<LoginResult> loginCallback;
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
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (loginCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
        }
        if (registrationCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
        }
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
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
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

        final Registrator registrator = retrofitClient().create(Registrator.class);
        final Call<RegistrationResult> call = registrator.registrate(new Login(login,password));
        registrationCallbackId = NetworkThread.getInstance().registerCallback(registrationCallback);
        NetworkThread.getInstance().execute(call, registrationCallbackId);
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
        final Loginner loginner = retrofitClient().create(Loginner.class);
        final Call<LoginResult> call = loginner.login(new Login(login,password));
        loginCallbackId = NetworkThread.getInstance().registerCallback(loginCallback);
        NetworkThread.getInstance().execute(call,loginCallbackId);
    }

    public void onLoginResult(LoginResult result) {
        if (result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity());
            goToMainActivity();
        }
        else  {
            Toast.makeText(getActivity(), "Данный логин уже занят. Попробуйте другой", Toast.LENGTH_SHORT).show();
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
            passwordInput.setError("Не менее 5 символов");
            valid = false;
        }
        return valid;
    }

    private void prepareCallbacks() {
        registrationCallback = new NetworkThread.ExecuteCallback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                progressDialog.dismiss();
                okhttp3.Headers headers = response.headers();
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(RegistrationResult result) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                progressDialog.dismiss();
                if (result.getCode() == 0){
                    onRegistrationResult(result);
                }

            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                Toast.makeText(getActivity(), "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }
        };

        loginCallback = new NetworkThread.ExecuteCallback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
            }

            @Override
            public void onFailure(Call<LoginResult> call, Response<LoginResult> response) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                Toast.makeText(getActivity(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(LoginResult result) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                onLoginResult(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                Toast.makeText(getActivity(), "Ошибка соединения с сервером", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
