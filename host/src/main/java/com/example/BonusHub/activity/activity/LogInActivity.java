package com.example.BonusHub.activity.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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

/**
 * Created by mike on 15.04.17.
 */

public class LogInActivity extends BaseActivity implements StackListner {
    private final static String TAG = LogInActivity.class.getSimpleName();
    private Toolbar mToolbar;

    static {
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectActivityLeaks()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStackListner(this);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");

        Log.d("Login", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        if (!AuthUtils.isAuthorized(this)) {
            setupLogInFragment();
            Log.d("Login go logfrag", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        }
        else if (!AuthUtils.isHosted(this)) {
            setupLogInFragment();
            setupStartFragment();
            Log.d("Login go start", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
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
