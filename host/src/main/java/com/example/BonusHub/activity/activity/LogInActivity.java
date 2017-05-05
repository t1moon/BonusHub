package com.example.BonusHub.activity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BonusHub.activity.LoginResult;
import com.example.BonusHub.activity.Loginner;
import com.example.BonusHub.activity.Login;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.timur.BonusHub.R;

import retrofit2.Call;

import static com.example.BonusHub.activity.RetrofitFactory.retrofitBarmen;

/**
 * Created by mike on 15.04.17.
 */

public class LogInActivity extends AppCompatActivity {
    private Button logInButton;
    private EditText loginInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInput = (EditText) findViewById(R.id.login_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        logInButton = (Button) findViewById(R.id.btn_login);
        logInButton.setOnClickListener(onLogInClickListener);
    }

    @Override
    protected void onDestroy() {
        //loginer.setCallback(null);
        super.onDestroy();
    }

    private final View.OnClickListener onLogInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logIn();
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
        Toast.makeText(this, "успех", Toast.LENGTH_SHORT).show();
    }
}
