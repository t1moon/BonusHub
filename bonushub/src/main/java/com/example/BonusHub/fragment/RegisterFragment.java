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
import static com.example.BonusHub.retrofit.RetrofitFactory.retrofitHost;

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
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                final CommonApiInterface commonApiInterface = retrofitHost().create(CommonApiInterface.class);
                call = commonApiInterface.registrate(new Login(login,password));
                break;
            case "Staff":
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
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                final CommonApiInterface commonApiInterface = retrofitCommon().create(CommonApiInterface.class);
                call = commonApiInterface.login(new Login(login,password));
                break;
            case "Staff":
                break;
        }
        if (loginCallbackId == null && call != null) {
            loginCallbackId = NetworkThread.getInstance().registerCallback(loginCallback);
            NetworkThread.getInstance().execute(call, loginCallbackId);
        }
    }

    public void onLoginResult(LoginResponse result) {
        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();

        if (result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity().getApplicationContext());
            if (!AuthUtils.getRole(logInActivity).equals("Host")) {
                goToMainActivity();
            } else {
                if (result.getHostId() == null) {
                    AuthUtils.setHosted(getActivity().getApplicationContext(), false);
                    goToStartFragment();
                    getActivity().getPreferences(MODE_PRIVATE).edit()
                            .putString("user_ident", result.getUserId()).apply();
                }
                else {
                    AuthUtils.setHosted(getActivity().getApplicationContext(), true);
                    getActivity().getPreferences(MODE_PRIVATE).edit()
                            .putString("host_ident", result.getHostId()).apply();
                    getActivity().getPreferences(MODE_PRIVATE).edit()
                            .putString("user_ident", result.getUserId()).apply();
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
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                NetworkThread.getInstance().unRegisterCallback(registrationCallbackId);
                registrationCallbackId = null;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), response.code(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
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
