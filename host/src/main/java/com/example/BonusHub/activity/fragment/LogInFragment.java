package com.example.BonusHub.activity.fragment;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.BonusHub.activity.Login;
import com.example.BonusHub.activity.LoginResult;
import com.example.BonusHub.activity.Loginner;
import com.example.BonusHub.activity.activity.LogInActivity;
import com.example.BonusHub.activity.activity.MainActivity;
import com.example.BonusHub.activity.executors.CreateHostExecutor;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.FragmentType;
import com.example.bonuslib.host.Host;
import com.example.timur.BonusHub.R;

import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;
import static com.example.BonusHub.activity.RetrofitFactory.retrofitBarmen;

/**
 * Created by mike on 05.05.17.
 */

public class LogInFragment extends Fragment {
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

    public void goToStartFragment() {
        logInActivity.setCurrentFragment(FragmentType.StartHost);
        logInActivity.pushFragment(new StartFragment(), false);
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
        final Loginner loginner = retrofitBarmen().create(Loginner.class);
        final Call<LoginResult> call = loginner.login(new Login(login,password));
        NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult result) {
                if (result.getCode() == 0){
                    onLoginResult(Boolean.TRUE);
                }

            }

            @Override
            public void onError(Exception ex) {

            }
        });
    }

    public void onLoginResult(boolean success) {
        Toast.makeText(getActivity(), "успех", Toast.LENGTH_SHORT).show();
        goToStartFragment();
    }
}
