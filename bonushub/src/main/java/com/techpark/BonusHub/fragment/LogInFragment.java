package com.techpark.BonusHub.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techpark.BonusHub.activity.StaffMainActivity;
import com.techpark.BonusHub.retrofit.CommonApiInterface;
import com.techpark.BonusHub.retrofit.getInfo.GetInfoResponse;
import com.techpark.BonusHub.utils.AuthUtils;
import com.techpark.BonusHub.retrofit.auth.Login;
import com.techpark.BonusHub.retrofit.auth.LoginResponse;
import com.techpark.BonusHub.activity.LogInActivity;
import com.techpark.BonusHub.activity.HostMainActivity;
import com.techpark.BonusHub.threadManager.NetworkThread;
import com.techpark.BonusHub.utils.FragmentType;
import com.techpark.timur.BonusHub.R;

import retrofit2.Call;
import retrofit2.Response;

import static com.techpark.BonusHub.retrofit.RetrofitFactory.retrofitCommon;

public class LogInFragment extends Fragment {
    private LogInActivity logInActivity;

    private static NetworkThread.ExecuteCallback<LoginResponse> loginCallback;
    private Integer loginCallbackId;
    private static NetworkThread.ExecuteCallback<GetInfoResponse> netInfoCallback;
    private Integer netInfoCallbackId;

    private Button logInButton;
    private TextView registrationButton;
    private EditText loginInput;
    private EditText passwordInput;
    private ProgressDialog progressDialog;
    View rootView;

    public LogInFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        prepareCallbacks();
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        if (progressDialog != null)
            progressDialog.dismiss();
        if (loginCallbackId != null)
            NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        loginInput = (EditText) rootView.findViewById(R.id.login_input);
        passwordInput = (EditText) rootView.findViewById(R.id.password_input);
        logInButton = (Button) rootView.findViewById(R.id.btn_login);
        logInButton.setOnClickListener(onLogInClickListener);
        registrationButton = (TextView) rootView.findViewById(R.id.link_signup);
        registrationButton.setOnClickListener(onRegistrationClickListener);
        setHasOptionsMenu(true);

        return rootView;
    }

    private final View.OnClickListener onLogInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logIn();
        }
    };

    private final View.OnClickListener onRegistrationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            goToRegisterFragment();
        }
    };

    private void logIn() {
        final String login = loginInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!validate()) {
            return;
        }

        progressDialog = new ProgressDialog(logInActivity);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Аутентификация...");
        progressDialog.show();


        Call<LoginResponse> call = null;
        final CommonApiInterface commonApiInterface = retrofitCommon().create(CommonApiInterface.class);
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                call = commonApiInterface.login(new Login(login, password));
                break;
            case "Staff":
                call = commonApiInterface.login(new Login(login, password));
                break;
        }
        if (loginCallbackId == null && call != null) {
            loginCallbackId = NetworkThread.getInstance().registerCallback(loginCallback);
            NetworkThread.getInstance().execute(call, loginCallbackId);
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

        if (password.isEmpty() || password.length() <= 5) {
            passwordInput.setError("Не менее 6 символов");
            valid = false;
        }
        return valid;
    }

    public void goToRegisterFragment() {
        logInActivity.pushFragment(new RegisterFragment(), true);
    }

    public void goToStartFragment() {
        logInActivity.setCurrentFragment(FragmentType.StartHost);
        logInActivity.pushFragment(new StartFragment(), false);
    }

    public void goToQr() {
        logInActivity.pushFragment(new QRFragment(), true);
    }

    public void goToMainActivity() {
        Intent intent = null;
        switch (AuthUtils.getRole(logInActivity)) {
            case "Host":
                intent = new Intent(getActivity(), HostMainActivity.class);
                break;
            case "Staff":
                intent = new Intent(getActivity(), StaffMainActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void prepareCallbacks() {

        loginCallback = new NetworkThread.ExecuteCallback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity().getApplicationContext(), cookie);
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Response<LoginResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                progressDialog.dismiss();
                if (response.code() == 400) {
                    Toast.makeText(getActivity(), "Не указан логин или пароль", Toast.LENGTH_SHORT).show();
                }
                else if(response.code() >= 500) {
                    Toast.makeText(getActivity(), "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(LoginResponse result) {
                onLoginResult(result);
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(loginCallbackId);
                loginCallbackId = null;
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Ошибка соединения с сервером. Проверьте интернет подключение.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void onLoginResult(LoginResponse result) {
        //Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        AuthUtils.setLogin(getActivity().getApplicationContext(), loginInput.getText().toString());
        if (result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity().getApplicationContext());
            if (!AuthUtils.getRole(getActivity().getApplicationContext()).equals("Host")) {
                // staff
                AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
                goToQr();
            } else {
                if (result.getHostId() == null) {
                    AuthUtils.setHosted(getActivity().getApplicationContext(), false);
                    AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
                    goToStartFragment();
                }
                else {
                    AuthUtils.setUserId(getActivity().getApplicationContext(), result.getUserId());
                    AuthUtils.setHostId(getActivity().getApplicationContext(), result.getHostId());
                    AuthUtils.setHosted(getActivity().getApplicationContext(), true);
                    goToMainActivity();
                }
            }
        }
        else if (result.getCode() == 1) {
            Toast.makeText(getActivity(), "Неверная пара логин/пароль", Toast.LENGTH_SHORT).show();
        }
    }
}
