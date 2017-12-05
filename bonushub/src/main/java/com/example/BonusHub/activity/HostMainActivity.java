package com.example.BonusHub.activity;

import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.BonusHub.fragment.EditFragment;
import com.example.BonusHub.fragment.ScanQrFragment;
import com.example.BonusHub.fragment.StaffListFragment;
import com.example.BonusHub.retrofit.CommonApiInterface;
import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.MyApplication;
import com.example.BonusHub.retrofit.HostApiInterface;
import com.example.BonusHub.retrofit.auth.LogoutResponse;
import com.example.BonusHub.fragment.OwnerSettingsFragment;
import com.example.BonusHub.fragment.ProfileFragment;
import com.example.BonusHub.fragment.StatisticFragment;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.FragmentType;
import com.example.BonusHub.utils.StackListner;
import com.example.timur.BonusHub.R;

import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.BonusHub.retrofit.RetrofitFactory.retrofitHost;

public class HostMainActivity extends BaseActivity implements StackListner {
    private final static String TAG = HostMainActivity.class.getSimpleName();
    private NetworkThread.ExecuteCallback<LogoutResponse> logoutCallback;
    private Integer logoutCallbackId;

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    public final static int MENUITEM_READ_QR = 0;
    public final static int MENUITEM_SHOW_PROFILE = 1;
    public final static int MENUITEM_OWNER_SETTINGS = 2;
    public final static int MENUITEM_STATISTIC = 3;
    public final static int MENUITEM_STAFF = 4;
    public final static int MENUITEM_LOGOUT = 5;

//    static {
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectActivityLeaks()
//                .penaltyLog()
//                .penaltyDeath()
//                .build()
//        );
//    }
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prepareCallbacks();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setStackListner(this);
        Log.d("Main", "auth" + AuthUtils.isAuthorized(this) + " " + AuthUtils.isHosted(this));
        if (!AuthUtils.isAuthorized(this)) {
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        nvDrawer = (NavigationView) this.findViewById(R.id.navigation_view);
        View header = nvDrawer.getHeaderView(0);
        TextView profileName = (TextView) header.findViewById(R.id.tv_profile_name);
        profileName.setText(AuthUtils.getLogin(this.getApplicationContext()));

        setupProfileFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logoutCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(logoutCallbackId);
        }
    }

    private void goToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");                                        // necessary
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the fab when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    fab.hide();
                    isShow = true;
                } else if (isShow) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerResId());
                    if (fragment instanceof ProfileFragment ||
                            fragment instanceof EditFragment)
                        fab.show();
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


    private void setupDrawerContent(final NavigationView navigationView) {
        Menu drawerMenu = navigationView.getMenu();

        drawerMenu.add(0, MENUITEM_READ_QR, 0, "Считать QR-код");
        drawerMenu.add(0, MENUITEM_SHOW_PROFILE, 1, "Профиль заведения");
        drawerMenu.add(0, MENUITEM_OWNER_SETTINGS, 2, "Параметры акций");
        //drawerMenu.add(0, MENUITEM_STATISTIC, 3, "Статистика");
        drawerMenu.add(0, MENUITEM_STAFF, 4, "Сотрудники");
        drawerMenu.add(0, MENUITEM_LOGOUT, 5, "Выход");

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
//            case MENUITEM_STATISTIC:
//                fragment = new StatisticFragment();
//                pushFragment(fragment, true);
//                break;
            case MENUITEM_SHOW_PROFILE:
                if (fragment.getClass() != ProfileFragment.class) {
                    setCurrentFragment(FragmentType.ProfileHost);
                    fragment = new ProfileFragment();
                    pushFragment(fragment, false);
                }
                break;

            case MENUITEM_OWNER_SETTINGS:
                if (fragment.getClass() != OwnerSettingsFragment.class) {
                    fragment = new OwnerSettingsFragment();
                    pushFragment(fragment, true);
                }
                break;
            case MENUITEM_STAFF:
                if (fragment.getClass() != OwnerSettingsFragment.class) {
                    fragment = new StaffListFragment();
                    pushFragment(fragment, true);
                }
                break;

            case MENUITEM_LOGOUT:
                final CommonApiInterface commonApiInterface = retrofitHost().create(CommonApiInterface.class);
                final Call<LogoutResponse> call = commonApiInterface.logout(AuthUtils.getCookie(this));
                logoutCallbackId = NetworkThread.getInstance().registerCallback(logoutCallback);
                NetworkThread.getInstance().execute(call, logoutCallbackId);
                break;
        }
        if (fragment != null) {
            menuItem.setChecked(true);  // Highlight the selected item has been done by NavigationView
            setTitle(menuItem.getTitle());  // Set action bar title
            mDrawer.closeDrawers();
            fab.setVisibility(View.GONE);
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
        Toast.makeText(this, "Вы успешно вышли из системы", Toast.LENGTH_SHORT).show();
        AuthUtils.logout(this);
        goToLogIn();
    }

    // Method to manually check connection status
    public boolean hasConnection() {
        boolean isConnected = isConnected(MyApplication.getInstance());
        if (!isConnected)
            showSnack(false);
        return isConnected;
    }


    // Showing the status in Snackbar
    public void showSnack(boolean isConnected) {
        String message;

        if (isConnected) {
            message = "Обновлено";
        } else {
            message = "Нет соединения с интернетом";
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    // Showing the status in Snackbar
    public void showSnack(String message) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void prepareCallbacks() {
        logoutCallback = new NetworkThread.ExecuteCallback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(logoutCallbackId);
                logoutCallbackId = null;
                AuthUtils.logout(HostMainActivity.this.getApplicationContext());
                AuthUtils.setHosted(HostMainActivity.this.getApplicationContext(), false);
                goToLogIn();
            }

            @Override
            public void onSuccess(LogoutResponse result) {
                NetworkThread.getInstance().unRegisterCallback(logoutCallbackId);
                logoutCallbackId = null;
                onLogoutResult();
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(logoutCallbackId);
                logoutCallbackId = null;
                Toast.makeText(HostMainActivity.this, "Ошибка соединения с сервером. Проверьте интернет подключение.", Toast.LENGTH_SHORT).show();
                AuthUtils.logout(HostMainActivity.this.getApplicationContext());
                AuthUtils.setHosted(HostMainActivity.this.getApplicationContext(), false);
                goToLogIn();
            }
        };
    }
}
