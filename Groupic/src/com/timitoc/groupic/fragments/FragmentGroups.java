package com.timitoc.groupic.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.GroupsFragmentModel;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.utils.Global;

public class FragmentGroups extends Fragment {
    View mainView;
    GroupsFragmentModel model;

    public FragmentGroups() {
        //this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_groups, container, false);

        setButtonEvent((Button) mainView.findViewById(R.id.your_groups), 0);
        setButtonEvent((Button) mainView.findViewById(R.id.search_groups), 1);
        setButtonEvent((Button) mainView.findViewById(R.id.create_new), 2);
        Global.onRefreshMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                setDisplay(0);
            }
        };
        Global.onAddMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                setDisplay(2);
            }
        };



        if (getArguments() != null && getArguments().containsKey("groups-model"))
            model = (GroupsFragmentModel) this.getArguments().getSerializable("groups-model");
        if (savedInstanceState == null)
            setDisplay(0);

        return mainView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        System.out.println("Hidden chanded in big framgena,t groupsd");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("groups-model", model);
    }


    public void setButtonEvent(Button button, final int position) {
        System.out.println("Setting " + position);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDisplay(position);
            }
        });
    }

    public void setDisplay(int position) {
        Fragment fragment = null;
        Bundle args;
        switch (position) {
            case 0:
                fragment = new FragmentMyGroups();
                break;
            case 1:
                fragment = new SearchGroupsFragment();
                args = new Bundle();
                args.putSerializable("search-model", model.searchModel);
                fragment.setArguments(args);
                break;
            case 2:
                fragment = new CreateNewGroupFragment();
                args = new Bundle();
                args.putSerializable("create-model", model.createModel);
                fragment.setArguments(args);
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.groups_frame_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

}