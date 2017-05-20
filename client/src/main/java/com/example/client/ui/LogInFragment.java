package com.example.client.ui;

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
import com.example.bonuslib.FragmentType;
import com.example.client.AuthUtils;
import com.example.client.activity.MainActivity;
import com.example.client.R;
import com.example.client.retrofit.login.LoginResult;
import com.example.client.retrofit.login.Loginner;
import com.example.client.threadManager.NetworkThread;
import com.example.client.retrofit.Login;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.client.api.RetrofitFactory.retrofitClient;

public class LogInFragment extends Fragment {
    private static final String LOGIN_PREFERENCES = "LoginData";
    private LogInActivity logInActivity;

    private Button logInButton;
    private Button registrationButton;
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
        registrationButton = (Button) rootView.findViewById(R.id.btn_registration);
        registrationButton.setOnClickListener(onRegistrationClickListener);
        setHasOptionsMenu(true);

        return rootView;
    }

    public void goToRegisterFragment() {
        logInActivity.setCurrentFragment(FragmentType.RegisterFragment);
        logInActivity.pushFragment(new RegisterFragment(), false);
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

        final Loginner loginner = retrofitClient().create(Loginner.class);
        final Call<LoginResult> call = loginner.login(new Login(login,password));

        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<LoginResult>() {
            @Override
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                String cookie = response.headers().get("Set-Cookie");
                AuthUtils.setCookie(getActivity(), cookie);
                Log.d("кука", "кука0");
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
            goToMainActivity();
        }
        else if (result.getCode() == 0){
            AuthUtils.setAuthorized(getActivity());
            goToMainActivity();
        }
    }
}
