package com.example.BonusHub.activity.activity;

import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.BonusHub.activity.fragment.ProfileFragment;
import com.example.BonusHub.activity.fragment.StartFragment;
import com.example.BonusHub.activity.fragment.ScanQrFragment;
import com.example.bonuslib.BaseActivity;
import com.example.bonuslib.FragmentType;
import com.example.timur.BonusHub.R;

public class MainActivity extends BaseActivity {
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private AppBarLayout appBarLayout;
    public final static int MENUITEM_READ_QR = 0;
    public final static int MENUITEM_SHOW_PROFILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
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
        setupStartFragment();
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

    private void setupStartFragment() {
        Fragment fragment;
        int host_id = this.getPreferences(MODE_PRIVATE).getInt("host_id", -1);
        if (host_id != -1) {
            setCurrentFragment(FragmentType.ProfileHost);
            fragment = new ProfileFragment();
        } else {
            setCurrentFragment(FragmentType.StartHost);
            fragment = new StartFragment();
        }
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
        drawerMenu.add(0, 0, 0, "Считать QR-код");
        drawerMenu.add(0, 1, 1, "Профиль заведения");


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
                if (fragment.getClass() != ScanQrFragment.class) {
                    fragment = new ScanQrFragment();
                    pushFragment(fragment, true);
                }
                break;
            case MENUITEM_SHOW_PROFILE:
                if (getCurrentStackSize() > 1) {
                    popWholeStack();
                    break;
                }
                if (fragment.getClass() != ProfileFragment.class) {
                    setCurrentFragment(FragmentType.ProfileHost);
                    fragment = new ProfileFragment();
                    pushFragment(fragment, false);
                }
                break;
        }
        if (fragment != null) {
            menuItem.setChecked(true);  // Highlight the selected item has been done by NavigationView
            setTitle(menuItem.getTitle());  // Set action bar title
            mDrawer.closeDrawers();
        }
        mDrawer.closeDrawers();
    }

    @Override
    protected int getFragmentContainerResId() {
        return R.id.container_body;
    }



}
