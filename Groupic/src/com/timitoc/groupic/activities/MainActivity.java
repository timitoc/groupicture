package com.timitoc.groupic.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.NavDrawerListAdapter;
import com.timitoc.groupic.fragments.*;
import com.timitoc.groupic.fragments.about.FragmentAbout;
import com.timitoc.groupic.fragments.help.FragmentHelp;
import com.timitoc.groupic.models.GroupsFragmentModel;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.models.NavDrawerItem;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.Global;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;

    public GroupsFragmentModel groupsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Global.initializeSettings(this);

	    mTitle = "closed";
        mDrawerTitle = "opened";

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        //for (String s : navMenuTitles)
            ///System.out.println(s);

        // nav drawer icons from resources
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

        for (int i = 0; i < 7; i++)
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
//        // Rate me
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
//        // Go premium
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
//        // About
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
//        // Help
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
//        // Log out
//        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));


        navMenuIcons.recycle();
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        if (savedInstanceState == null) {
            // on first time display view for first nav item
            groupsModel = new GroupsFragmentModel();
        }
        else {
            groupsModel = (GroupsFragmentModel) savedInstanceState.getSerializable("groups-model");
        }
        if (savedInstanceState == null)
            displayView(0);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }

    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        Bundle args;
        switch (position) {
            case 0:
                fragment = new FragmentGroups();
                args = new Bundle();
                args.putSerializable("groups-model", groupsModel);
                fragment.setArguments(args);
                break;
            case 1:
                // fragment = new FragmentSecond();
                break;
            case 2:
                // fragment = new FragmentSecond();
                break;
            case 3:
                fragment = new FragmentOptions();
                break;
            case 4:
                fragment = new FragmentAbout();
                break;
            case 5:
                fragment = new FragmentHelp();
                break;
            case 6:
                Global.want_login = false;
                Global.logging_out = true;
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        ConnectionStateManager.setController(menu.getItem(0));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.refresh:
                Global.onRefreshMenuItemClicked.run(); return true;
            case R.id.add_menu_item:
                Global.onAddMenuItemClicked.run(); return true;
            case R.id.delete:
                Global.deleteIconIsPressed = !Global.deleteIconIsPressed;
                if (Global.deleteIconIsPressed)
                    item.setIcon(R.drawable.delete_pressed);
                else
                    item.setIcon(R.drawable.delete);
                System.out.println("miPhoto"); return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.miCompose).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("groups-model", groupsModel);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        Global.onBaseActivityDestroyed();
        super.onDestroy();

    }
}
