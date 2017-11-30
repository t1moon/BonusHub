package com.techpark.BonusHub.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.techpark.BonusHub.MyApplication;
import com.techpark.BonusHub.fragment.QRFragment;
import com.techpark.BonusHub.fragment.RoleFragment;
import com.techpark.BonusHub.fragment.StartFragment;
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
            setupRoleFragment();
        }
        else {
            if(AuthUtils.getRole(this).equals("Host") && !AuthUtils.isHosted(this))
                setupStartFragment();
            else if((AuthUtils.getRole(this).equals("Staff"))){
                setupQRFragment();
            }
            else {
                goToMainActivity();
            }
        }
    }

    public void goToMainActivity() {
        Intent intent = null;
        switch (AuthUtils.getRole(this)) {
            case "Host":
                intent = new Intent(this, HostMainActivity.class);
                break;
            case "Staff":
                intent = new Intent(this, StaffMainActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupRoleFragment() {
        setCurrentFragment(FragmentType.RoleFragment);
        pushFragment(new RoleFragment(), true);
    }

    private void setupStartFragment() {
        setCurrentFragment(FragmentType.StartHost);
        pushFragment(new RoleFragment(), true);
        pushFragment(new StartFragment(), true);
    }

    private void setupQRFragment() {
        setCurrentFragment(FragmentType.StaffQR);
        pushFragment(new RoleFragment(), true);
        pushFragment(new QRFragment(), true);
    }

    public boolean hasConnection() {
        boolean isConnected = isConnected(MyApplication.getInstance());
        return isConnected;
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
