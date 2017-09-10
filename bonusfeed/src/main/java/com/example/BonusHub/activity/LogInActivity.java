package com.example.BonusHub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.BonusHub.fragment.LogInFragment;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.utils.FragmentType;
import com.example.BonusHub.utils.StackListner;
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
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");

        if (!AuthUtils.isAuthorized(this)) {
            setupLoginFragment();
        }
        else {
            goToMainActivity();
        }
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, ClientMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupLoginFragment() {
        setCurrentFragment(FragmentType.RoleFragment);
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
