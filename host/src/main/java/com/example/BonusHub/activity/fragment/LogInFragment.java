package com.example.BonusHub.activity.fragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.Login;
import com.example.BonusHub.activity.api.login.LoginResult;
import com.example.BonusHub.activity.api.login.Loginner;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.FragmentType;
import com.example.timur.BonusHub.R;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.BonusHub.activity.api.RetrofitFactory.retrofitBarmen;

public class LogInFragment extends Fragment {
    private static final String LOGIN_PREFERENCES = "LoginData";
    private LogInActivity logInActivity;

    private Button logInButton;
    private TextView registrationButton;
    private EditText loginInput;
    private EditText passwordInput;
    View rootView;

    public LogInFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
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

    public void goToRegisterFragment() {
        logInActivity.pushFragment(new RegisterFragment(), true);
    }

    public void goToStartFragment() {
        logInActivity.setCurrentFragment(FragmentType.StartHost);
        logInActivity.pushFragment(new StartFragment(), false);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
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

        final ProgressDialog progressDialog = new ProgressDialog(logInActivity);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Аутентификация...");
        progressDialog.show();


        final Loginner loginner = retrofitBarmen().create(Loginner.class);
        final Call<LoginResult> call = loginner.login(new Login(login,password));

        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
                Log.d("кука", "кука0");
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d("кука", "кука1");
            }

            @Override
            public void onSuccess(LoginResult result) {
                onLoginResult(result);
            }

            @Override
            public void onError(Exception ex) {
                Log.d("кука", "кука3");
            }
        });
    }

    public void onLoginResult(LoginResult result) {
        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        if (result.isHosted() == false && result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity());
            goToStartFragment();
        }
        else if (result.getCode() == 0){
            AuthUtils.setAuthorized(getActivity());
            AuthUtils.setHosted(getActivity());
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
            passwordInput.setError("Не менее 5 символов");
            valid = false;
        }
        return valid;
    }
}
