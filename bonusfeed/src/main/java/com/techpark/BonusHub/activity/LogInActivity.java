package com.techpark.BonusHub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techpark.BonusHub.fragment.LogInFragment;
import com.techpark.BonusHub.utils.AuthUtils;
import com.techpark.BonusHub.utils.FragmentType;
import com.techpark.BonusHub.utils.StackListner;
import com.techpark.timur.BonusHub.R;

public class LogInActivity extends BaseActivity implements StackListner {
//    static {
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectActivityLeaks()
//                .penaltyLog()
//                .penaltyDeath()
//                .build()
//        );
//    }

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
