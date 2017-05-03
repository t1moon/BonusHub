package com.example.client;

import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.bonuslib.client.Client;
import com.example.bonuslib.db.HelperFactory;
import com.example.bonuslib.host.Host;
import com.example.client.fragment.ListHostFragment;
import com.example.client.fragment.QRFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarLayout appBarLayout;


    public static int CLIENT_ID = 7;
    public static String CLIENT_NAME = "Timur";
    public static String CLIENT_IDENTIFICATOR = "QfgnJKEGNRojer";

    private static int client_id;

    public static int getClientId() {
        return client_id;
    }

    public static void setClient_id(int client_id) {
        MainActivity.client_id = client_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.getPreferences(MODE_PRIVATE).edit().putInt("client_id", -1).apply();
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
                            appBarLayout.setExpanded(false);

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
                            appBarLayout.setExpanded(true);
                        }
                    }
                }
        );

        setupClient();
        populateHosts();
        setupStartFragment();

    }

    private void setupClient() {
        int client_id = this.getPreferences(MODE_PRIVATE).getInt("client_id", -1);

        if (client_id == -1) {
            try {
                client_id = HelperFactory.getHelper().getClientDAO().createClient(CLIENT_NAME, CLIENT_IDENTIFICATOR);
                Log.d("MA, create",Integer.toString(client_id) );
                this.getPreferences(MODE_PRIVATE).edit()
                        .putInt("client_id", client_id).apply();
                setClient_id(client_id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            setClient_id(client_id);
        }
    }
    private void populateHosts() {
        Client client = null;
        try {
            client = HelperFactory.getHelper().getClientDAO().getClientById(getClientId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Host> hostList = new ArrayList<>();
        Host host = new Host("Surf coffee", "Best", "Baumanskaya");
        hostList.add(host);
        host = new Host("One bucks", "Sweety", "Tverskay");
        hostList.add(host);
        try {
            for (Host item: hostList) {
                HelperFactory.getHelper().getHostDAO().createHost(item);
                HelperFactory.getHelper().getClientHostDAO().createClientHost(client, item, 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
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

    private void setupStartFragment() {
        Fragment fragment = null;
        try {
            try {
                fragment = (Fragment) ListHostFragment.class.newInstance();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
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
        drawerMenu.add(0,0,0, "Показать QR-код");
        drawerMenu.add(0,1,1, "Показать список заведений");

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
        Fragment fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case 0:
                fragmentClass = QRFragment.class;
                break;
            case 1:
                fragmentClass = ListHostFragment.class;
                break;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }


}
