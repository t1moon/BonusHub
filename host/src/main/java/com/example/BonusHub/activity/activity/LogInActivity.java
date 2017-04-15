package com.example.BonusHub.activity.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.BonusHub.activity.threadManager.LoginThreadManager;
import com.example.timur.BonusHub.R;

/**
 * Created by mike on 15.04.17.
 */

public class LogInActivity extends AppCompatActivity {
    private static LoginThreadManager loginer;
    private Button logInButton;
    private EditText loginInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInput = (EditText) findViewById(R.id.login_input);
        loginInput = (EditText) findViewById(R.id.password_input);
        logInButton = (Button) findViewById(R.id.btn_login);
        logInButton.setOnClickListener(onLogInClickListener);
        loginer = LoginThreadManager.getInstance();
        loginer.setCallback(new LoginThreadManager.Callback() {
            @Override
            public void onLoaded(boolean result) {
                onLoginResult(result);
            }
        });
    }

    @Override
    protected void onDestroy() {
        loginer.setCallback(null);
        super.onDestroy();
    }

    private final View.OnClickListener onLogInClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            logIn();
        }
    };

    private void logIn() {
        loginer.load(this);

    }

    public void onLoginResult(boolean success) {
        Toast.makeText(this, "успех", Toast.LENGTH_SHORT).show();
    }
}
