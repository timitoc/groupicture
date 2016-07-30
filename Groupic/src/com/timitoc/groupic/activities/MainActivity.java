package com.timitoc.groupic.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.NavDrawerListAdapter;
import com.timitoc.groupic.fragments.*;
import com.timitoc.groupic.fragments.AboutFragment;
import com.timitoc.groupic.fragments.HelpFragment;
import com.timitoc.groupic.models.GroupsFragmentModel;
import com.timitoc.groupic.models.NavDrawerItem;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.Global;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
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

	    mTitle = "Menu";
        mDrawerTitle = "Menu";

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

        for (int i = 0; i < navMenuTitles.length; i++)
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));

        navMenuIcons.recycle();
        NavDrawerListAdapter adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        if (savedInstanceState == null) {
            groupsModel = new GroupsFragmentModel();
        }
        else {
            groupsModel = (GroupsFragmentModel) savedInstanceState.getSerializable("groups-model");
        }
        if (savedInstanceState == null)
            displayView(0);
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() <= 1) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }
    }

    private void displayView(int position) {
        Fragment fragment = null;
        Bundle args;
        switch (position) {
            case 0:
                fragment = new GroupsFragment();
                args = new Bundle();
                args.putSerializable("groups-model", groupsModel);
                fragment.setArguments(args);
                break;
            case 1:
                // fragment = new TextFragment();
                break;
            case 2:
                // fragment = new TextFragment();
                break;
            case 3:
                fragment = new OptionsFragment();
                break;
            case 4:
                fragment = new AboutFragment();
                break;
            case 5:
                fragment = new HelpFragment();
                break;
            case 6:
                fragment = new CreditsFragment();
                break;
            case 7:
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentById(R.id.frame_container) == null) {
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
            else {
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
            }


            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
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
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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
                    item.setIcon(R.drawable.delete); return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        Global.onBaseActivityDestroyed();
        super.onDestroy();
    }
}
