package com.timitoc.groupic.models;

import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.timitoc.groupic.R;

public class FragmentGroups extends Fragment {
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_groups, container, false);
        setDisplay(0);
        setButtonEvent((Button) mainView.findViewById(R.id.your_groups), 0);
        setButtonEvent((Button) mainView.findViewById(R.id.search_groups), 1);
        setButtonEvent((Button) mainView.findViewById(R.id.create_new), 2);
        return mainView;
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
        switch (position) {
            case 0:
                fragment = new FragmentMyGroups();
                System.out.println("0");
                break;
            case 1:
                fragment = new FragmentSecond();
                System.out.println("1");
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.groups_frame_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

}