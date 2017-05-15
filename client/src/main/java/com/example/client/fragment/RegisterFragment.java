package com.example.client.fragment;

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

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.Login;
import com.example.BonusHub.activity.api.login.LoginResult;
import com.example.BonusHub.activity.api.login.Loginner;
import com.example.BonusHub.activity.api.registration.RegistrationResult;
import com.example.BonusHub.activity.api.registration.Registrator;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.FragmentType;
import com.example.client.activity.LogInActivity;
import com.example.client.activity.MainActivity;
import com.example.client.R;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.BonusHub.activity.api.RetrofitFactory.retrofitBarmen;

/**
 * Created by mike on 05.05.17.
 */

public class RegisterFragment extends Fragment {
    private LogInActivity logInActivity;

    private Button logInButton;
    private Button registrationButton;
    private EditText loginInput;
    private EditText passwordInput;
    View rootView;

    public RegisterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInActivity = (LogInActivity) getActivity();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register, container, false);

        loginInput = (EditText) rootView.findViewById(R.id.login_input);
        passwordInput = (EditText) rootView.findViewById(R.id.password_input);
        logInButton = (Button) rootView.findViewById(R.id.btn_login);
        logInButton.setOnClickListener(onLogInClickListener);
        registrationButton = (Button) rootView.findViewById(R.id.btn_registrate);
        registrationButton.setOnClickListener(onRegistrationClickListener);
        setHasOptionsMenu(true);

        return rootView;
    }


    public void goToStartFragment() {
        logInActivity.setCurrentFragment(FragmentType.StartHost);
        logInActivity.pushFragment(new StartFragment(), false);
    }

    public void goToLoginFragment() {
        logInActivity.setCurrentFragment(FragmentType.LogInFragment);
        logInActivity.pushFragment(new LogInFragment(), false);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private final View.OnClickListener onLogInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            goToLoginFragment();
        }
    };

    private final View.OnClickListener onRegistrationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            registrate();
        }
    };

    private void registrate() {
        final String login = loginInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final Registrator registrator = retrofitBarmen().create(Registrator.class);
        final Call<RegistrationResult> call = registrator.registrate(new Login(login,password));
        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<RegistrationResult>() {
            @Override
            public void onResponse(Call<RegistrationResult> call, Response<RegistrationResult> response) {
                okhttp3.Headers headers = response.headers();
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
            }

            @Override
            public void onFailure(Call<RegistrationResult> call, Throwable t) {

            }

            @Override
            public void onSuccess(RegistrationResult result) {
                if (result.getCode() == 0){
                    onRegistrationResult(result);
                }

            }

            @Override
            public void onError(Exception ex) {

            }
        });
    }

    public void onRegistrationResult(RegistrationResult result) {
        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        if (result.getCode() == 0) {
            logIn();
        }
    }
    private void logIn() {
        final String login = loginInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final Loginner loginner = retrofitBarmen().create(Loginner.class);
        final Call<LoginResult> call = loginner.login(new Login(login,password));
        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                okhttp3.Headers headers = response.headers();
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
            }

            @Override
            public void onFailure(Call<LoginResult> call, Throwable t) {

            }

            @Override
            public void onSuccess(LoginResult result) {
                onLoginResult(result);
            }

            @Override
            public void onError(Exception ex) {
                Toast.makeText(getActivity(), "Возникли проблемы с авторизацией, попробуйте позже", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onLoginResult(LoginResult result) {
        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        if (result.isHosted() == false && result.getCode() == 0) {
            AuthUtils.setAuthorized(getActivity());
            Log.d("LogFrag go start", "auth" + AuthUtils.isAuthorized(getActivity()) + " " + result.isHosted());
            goToStartFragment();
        }
        else if (result.getCode() == 0){
            AuthUtils.setAuthorized(getActivity());
            AuthUtils.setHosted(getActivity());
            Log.d("LogFrag go main", "auth" + AuthUtils.isAuthorized(getActivity()) + " " + AuthUtils.isHosted(getActivity()));
            goToMainActivity();
        }
    }
}
