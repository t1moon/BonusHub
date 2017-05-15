package com.example.BonusHub.activity;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.BonusHub.AuthUtils;
import com.example.BonusHub.ac.fragment.LogInFragment;
import com.example.BonusHub.ac.fragment.StartFragment;
import com.example.bonuslib.BaseActivity;
import com.example.bonuslib.FragmentType;
import com.example.bonuslib.StackListner;
import com.example.timur.BonusHub.R;

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
        setContentView(R.layout.activity_main);

        setStackListner(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initCollapsingToolbar();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        Log.d("Login", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        if (!AuthUtils.isAuthorized(this)) {
            setupLogInFragment();
            Log.d("Login go logfrag", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        }
        else if (!AuthUtils.isHosted(this)) {
            setupStartFragment();
            Log.d("Login go start", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        }
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupLogInFragment() {
        setCurrentFragment(FragmentType.LogInFragment);
        pushFragment(new LogInFragment(), false);
    }

    private void setupStartFragment() {
        setCurrentFragment(FragmentType.StartHost);
        pushFragment(new StartFragment(), false);
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
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void homeStack() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // hide back button
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerToggle.syncState();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });
    }
}
