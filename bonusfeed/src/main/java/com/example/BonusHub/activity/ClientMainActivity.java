package com.example.BonusHub.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BonusHub.utils.AuthUtils;
import com.example.BonusHub.MyApplication;
import com.example.BonusHub.fragment.clientapp.ListHostFragment;
import com.example.BonusHub.fragment.clientapp.QRFragment;
import com.example.BonusHub.retrofit.ClientApiInterface;
import com.example.BonusHub.retrofit.auth.LogoutResponse;
import com.example.BonusHub.retrofit.clientapp.ClientInfoResponse;
import com.example.BonusHub.threadManager.NetworkThread;
import com.example.BonusHub.utils.FragmentType;
import com.example.BonusHub.utils.StackListner;
import com.example.BonusHub.db.HelperFactory;
import com.example.timur.BonusHub.R;

import java.util.jar.Attributes;

import retrofit2.Call;
import retrofit2.Response;

import static com.example.BonusHub.retrofit.RetrofitFactory.retrofitClient;

public class ClientMainActivity extends BaseActivity implements StackListner {

    private static NetworkThread.ExecuteCallback<LogoutResponse> logoutCallback;
    private Integer logoutCallbackId;
    private static NetworkThread.ExecuteCallback<ClientInfoResponse> clientCallback;
    private Integer clientCallbackId;

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private Menu menu;

    public final static int MENUITEM_QR = 0;
    public final static int MENUITEM_LISTHOST = 1;
    public final static int MENUITEM_LOGOUT = 2;

    private ClientMainActivity mainActivity;

    public final static String CLIENT_NAME = "CLIENT_NAME";
    public final static String CLIENT_IDENTIFICATOR = "CLIENT_IDENTIFICATOR";
    public final static String CLIENT_ID = "CLIENT_ID";

    private int client_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        setStackListner(this);
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

        // Setup behaviour for back home button, hamburger etc.
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            });
//                            appBarLayout.setExpanded(false);

                        } else {
                            //show hamburger
                            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                            drawerToggle.syncState();
                            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mDrawer.openDrawer(GravityCompat.START);
                                }
                            });
//                            appBarLayout.setExpanded(true);
                        }
                    }
                }
        );

        prepareCallbacks();
        setupClient();
        setupStartFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start_menu, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appBarLayout.setExpanded(false,false);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                appBarLayout.setExpanded(true);
                return false;
            }
        });
        this.menu = menu;
        return true;
    }

    public void showOverflowMenu(boolean showMenu){
        if(menu == null) {
            Log.d("menuNull", "Yes");
            return;
        }
        Log.d("menuNull", "No");
        menu.findItem(R.id.action_search).setVisible(showMenu);
        //menu.setItemVisible(R.id.main_menu_group, showMenu);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (logoutCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(logoutCallbackId);
        }
        if (clientCallbackId != null) {
            NetworkThread.getInstance().unRegisterCallback(clientCallbackId);
        }
    }

    private void goToLogIn() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupClient() {
        if (this.hasConnection()) {
            final ClientApiInterface clientApiInterface = retrofitClient().create(ClientApiInterface.class);
            final Call<ClientInfoResponse> call = clientApiInterface.getInfo(AuthUtils.getCookie(this));
            if (clientCallbackId == null) {
                clientCallbackId = NetworkThread.getInstance().registerCallback(clientCallback);
                NetworkThread.getInstance().execute(call, clientCallbackId);
            }
        }
        else {
            String name = mainActivity.getPreferences(MODE_PRIVATE).getString(CLIENT_NAME, "Name");
            NavigationView nvDrawer = (NavigationView) mainActivity.findViewById(R.id.navigation_view);
            View header = nvDrawer.getHeaderView(0);
            TextView profileName = (TextView) header.findViewById(R.id.tv_profile_name);
            if (name.equals("Name")) {
                showSnack(false);
            }
            profileName.setText(name);
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        collapsingToolbar =
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

    private void setupStartFragment() {
        setCurrentFragment(FragmentType.ListHost);
        pushFragment(new ListHostFragment(), true);
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
        drawerMenu.add(0, MENUITEM_QR, 0, "Показать QR-код");
        drawerMenu.add(0, MENUITEM_LISTHOST, 1, "Показать список заведений");
        drawerMenu.add(0, MENUITEM_LOGOUT, 2, "Выход");

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
            case MENUITEM_QR:
                fragment = new QRFragment();
                pushFragment(fragment, true);
                showOverflowMenu(false);
                break;
            case MENUITEM_LISTHOST:
                if (fragment.getClass() != ListHostFragment.class) {
                    setCurrentFragment(FragmentType.ListHost);
                    fragment = new ListHostFragment();
                    pushFragment(fragment, false);
                }
                break;
            case MENUITEM_LOGOUT:
                final ClientApiInterface logouter = retrofitClient().create(ClientApiInterface.class);
                final Call<LogoutResponse> call = logouter.logout(AuthUtils.getCookie(this));
                if (logoutCallbackId == null) {
                    logoutCallbackId = NetworkThread.getInstance().registerCallback(logoutCallback);
                    NetworkThread.getInstance().execute(call, logoutCallbackId);
                }
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

    private void onLogoutResult() {
        Toast.makeText(this, "Вы успешно вышли из системы", Toast.LENGTH_SHORT).show();
        AuthUtils.logout(this);
        goToLogIn();
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
            nvDrawer.getMenu().getItem(MENUITEM_LISTHOST).setChecked(true);
        }
    }

    // Method to manually check connection status
    public boolean hasConnection() {
        boolean isConnected = isConnected(MyApplication.getInstance());
        if (!isConnected)
            showSnack(false);
        return isConnected;
    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;

        if (isConnected) {
            message = "Есть соединение";
        } else {
            message = "Ожидание подключения";
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showError(Throwable error) {
        new AlertDialog.Builder(this)
                .setTitle("Упс!")
                .setMessage("Ошибка соединения с сервером. Проверьте интернет подключение.")
                .setPositiveButton("OK", null)
                .show();
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
                Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_SHORT).show();
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
                onLogoutResult();
            }
        };

        clientCallback = new NetworkThread.ExecuteCallback<ClientInfoResponse>() {

            @Override
            public void onResponse(Call<ClientInfoResponse> call, Response<ClientInfoResponse> response) {

            }

            @Override
            public void onFailure(Call<ClientInfoResponse> call, Response<ClientInfoResponse> response) {
                NetworkThread.getInstance().unRegisterCallback(clientCallbackId);
                clientCallbackId = null;
                if (response.code() == 403) {
                    Toast.makeText(mainActivity, "Пожалуйста, авторизуйтесь", Toast.LENGTH_SHORT).show();
                    AuthUtils.logout(mainActivity);
                    AuthUtils.setCookie(mainActivity, "");
                    goToLogIn();

                }
                else if(response.code() > 500) {
                    Toast.makeText(mainActivity, "Ошибка сервера. Попробуйте повторить запрос позже", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSuccess(ClientInfoResponse result) {
                NetworkThread.getInstance().unRegisterCallback(clientCallbackId);
                clientCallbackId = null;

                mainActivity.getPreferences(MODE_PRIVATE).edit().putString(CLIENT_NAME, result.getName())
                        .putString(CLIENT_IDENTIFICATOR, result.getIdentificator()).apply();

                try {
                    client_id = HelperFactory.getHelper().getClientDAO().createClient(CLIENT_NAME, CLIENT_IDENTIFICATOR);
                    Log.d("MA, create", Integer.toString(client_id));
                    mainActivity.getPreferences(MODE_PRIVATE).edit()
                            .putInt(CLIENT_ID, client_id).apply();
                } catch (java.sql.SQLException e) {
                    e.printStackTrace();
                }

                NavigationView nvDrawer = (NavigationView) mainActivity.findViewById(R.id.navigation_view);
                View header = nvDrawer.getHeaderView(0);
                TextView profileName = (TextView) header.findViewById(R.id.tv_profile_name);
                profileName.setText(mainActivity.getPreferences(MODE_PRIVATE).getString(CLIENT_NAME, "Name"));
            }

            @Override
            public void onError(Exception ex) {
                NetworkThread.getInstance().unRegisterCallback(clientCallbackId);
                clientCallbackId = null;
                showError(ex);
                //Toast.makeText(ClientMainActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        };

    }
}
