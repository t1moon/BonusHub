package com.example.BonusHub.activity.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.fragment.LogInFragment;
import com.example.BonusHub.activity.fragment.StartFragment;
import com.example.bonuslib.BaseActivity;
import com.example.bonuslib.FragmentType;
import com.example.bonuslib.StackListner;
import com.example.timur.BonusHub.R;

public class LogInActivity extends BaseActivity implements StackListner {
    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setStackListner(this);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("");

        if (!AuthUtils.isAuthorized(this)) {
            setupLogInFragment();
        }
        else if (!AuthUtils.isHosted(this)) {
            setupLogInFragment();
            setupStartFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupLogInFragment() {
        setCurrentFragment(FragmentType.LogInFragment);
        pushFragment(new LogInFragment(), true);
    }

    private void setupStartFragment() {
        setCurrentFragment(FragmentType.StartHost);
        pushFragment(new StartFragment(), true);
    }

    @Override
    protected int getFragmentContainerResId() {
        return R.id.container_body;
    }

    @Override
    public void deepStack() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void homeStack() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // hide back button
    }
}
