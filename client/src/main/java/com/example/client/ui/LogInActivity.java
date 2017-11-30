package com.techpark.client.ui;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techpark.client.AuthUtils;
import com.techpark.bonuslib.BaseActivity;
import com.techpark.bonuslib.FragmentType;
import com.techpark.bonuslib.StackListner;
import com.techpark.client.R;

/**
 * Created by mike on 15.04.17.
 */

public class LogInActivity extends BaseActivity implements StackListner {
    private static final String LOGIN_PREFERENCES = "LoginData";
    private final static String TAG = LogInActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarLayout appBarLayout;

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

        if (!AuthUtils.isAuthorized(this)) {
            setupLogInFragment();
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
