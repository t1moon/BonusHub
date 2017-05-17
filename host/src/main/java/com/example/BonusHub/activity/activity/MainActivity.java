package com.example.BonusHub.activity.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.BonusHub.activity.AuthUtils;
import com.example.BonusHub.activity.Login;
import com.example.BonusHub.activity.api.login.LoginResult;
import com.example.BonusHub.activity.api.login.Loginner;
import com.example.BonusHub.activity.api.login.LogoutResult;
import com.example.BonusHub.activity.api.login.Logouter;
import com.example.BonusHub.activity.fragment.OwnerSettingsFragment;
import com.example.BonusHub.activity.fragment.ProfileFragment;
import com.example.BonusHub.activity.fragment.ScanQrFragment;
import com.example.BonusHub.activity.threadManager.NetworkThread;
import com.example.bonuslib.BaseActivity;
import com.example.bonuslib.FragmentType;
import com.example.bonuslib.StackListner;
import com.example.timur.BonusHub.R;

import android.content.SharedPreferences;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.BonusHub.activity.api.RetrofitFactory.retrofitBarmen;

public class MainActivity extends BaseActivity implements StackListner {
    private static final String LOGIN_PREFERENCES = "LoginData";
    private final static String TAG = MainActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarLayout appBarLayout;
    public final static int MENUITEM_READ_QR = 0;
    public final static int MENUITEM_SHOW_PROFILE = 1;
    public final static int MENUITEM_OWNER_SETTINGS = 2;
    public final static int MENUITEM_LOGOUT = 3;


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
        Log.d("Main", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        if (!AuthUtils.isAuthorized(this) || !AuthUtils.isHosted(this)) {
            goToLogIn();
            return;
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initCollapsingToolbar();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.navigation_view);
        // Setup drawer view
        setupDrawerContent(nvDrawer);

        setupProfileFragment();
    }

    private void goToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
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

    private void setupProfileFragment() {
        Fragment fragment;
        setCurrentFragment(FragmentType.ProfileHost);
        fragment = new ProfileFragment();
        pushFragment(fragment, true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    private void setupDrawerContent(final NavigationView navigationView) {
        Menu drawerMenu = navigationView.getMenu();

        drawerMenu.add(0, MENUITEM_READ_QR, 0, "Считать QR-код");
        drawerMenu.add(0, MENUITEM_SHOW_PROFILE, 1, "Профиль заведения");
        drawerMenu.add(0, MENUITEM_OWNER_SETTINGS, 2, "Параметры акций");
        drawerMenu.add(0, MENUITEM_LOGOUT, 3, "Выход");

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                        uncheckAllMenuItems(navigationView);
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void uncheckAllMenuItems(NavigationView navigationView) {
        final Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_body);
        switch (menuItem.getItemId()) {
            case MENUITEM_READ_QR:
                fragment = new ScanQrFragment();
                pushFragment(fragment, true);
                break;
            case MENUITEM_SHOW_PROFILE:
                if (fragment.getClass() != ProfileFragment.class) {
                    setCurrentFragment(FragmentType.ProfileHost);
                    fragment = new ProfileFragment();
                    pushFragment(fragment, false);
                }
                break;

            case MENUITEM_OWNER_SETTINGS:
                if (fragment.getClass() != OwnerSettingsFragment.class) {
                    setCurrentFragment(FragmentType.OwnerSettings);
                    fragment = new OwnerSettingsFragment();
                    pushFragment(fragment, true);
                }
            case MENUITEM_LOGOUT:
                Toast.makeText(this, AuthUtils.getCookie(this), Toast.LENGTH_SHORT).show();
                final Logouter logouter = retrofitBarmen().create(Logouter.class);
                final Call<LogoutResult> call = logouter.logout(AuthUtils.getCookie(this));
                NetworkThread.getInstance().execute(call, new NetworkThread.ExecuteCallback<LogoutResult>() {
                    @Override
                    public void onResponse(Call<LogoutResult> call, Response<LogoutResult> response) {
                    }

                    @Override
                    public void onFailure(Call<LogoutResult> call, Throwable t) {
                    }

                    @Override
                    public void onSuccess(LogoutResult result) {
                        onLogoutResult();
                    }

                    @Override
                    public void onError(Exception ex) {
                        Toast.makeText(MainActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
        if (fragment != null) {
            menuItem.setChecked(true);  // Highlight the selected item has been done by NavigationView
            setTitle(menuItem.getTitle());  // Set action bar title
            mDrawer.closeDrawers();
        }

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
        uncheckAllMenuItems(nvDrawer);
        if (getCurrentFragment() == FragmentType.ProfileHost) {
            nvDrawer.getMenu().getItem(MENUITEM_SHOW_PROFILE).setChecked(true);
        }


    }

    private void onLogoutResult() {
        Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
        AuthUtils.logout(this);
        goToLogIn();
    }
}
