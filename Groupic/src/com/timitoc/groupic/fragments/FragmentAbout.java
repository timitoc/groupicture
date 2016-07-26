package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Global;

/**
 * Created by Cornel on 02.05.2016.
 */
public class FragmentAbout extends Fragment{
    View mainView;
    int displayScreen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_about, container, false);
        Global.onRefreshMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                setDisplay(0);
            }
        };
        if (savedInstanceState == null)
            displayScreen = 0;
        else {
            displayScreen = savedInstanceState.getInt("display");
        }
        setDisplay(displayScreen);
        setButtonEvent((Button) mainView.findViewById(R.id.about_us), 0);
        setButtonEvent((Button) mainView.findViewById(R.id.about_the_app), 1);
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
        Fragment fragment = new FragmentText();
        Bundle args = new Bundle();
        displayScreen = position;
        switch (position) {
            case 0:
                args.putString("param1", getResources().getString(R.string.about_us));
                break;
            case 1:
                args.putString("param1", getResources().getString(R.string.about_app));
                break;
            default:
                break;
        }
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.about_frame_container, fragment).commit();
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("display", displayScreen);
        super.onSaveInstanceState(savedInstanceState);
    }
}
