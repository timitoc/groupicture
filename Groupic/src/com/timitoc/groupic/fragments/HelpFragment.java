package com.timitoc.groupic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Global;

/**
 * Created by Cornel on 02.05.2016.
 */
public class HelpFragment extends Fragment{
    View mainView;
    int displayScreen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_help, container, false);
        if (savedInstanceState == null)
            displayScreen = 0;
        else {
            displayScreen = savedInstanceState.getInt("display");
        }
        setDisplay(displayScreen);
        Global.onRefreshMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                setDisplay(0);
            }
        };
        setButtonEvent((Button) mainView.findViewById(R.id.help_use1), 0);
        setButtonEvent((Button) mainView.findViewById(R.id.help_use2), 1);
        setButtonEvent((Button) mainView.findViewById(R.id.help_use3), 2);
        setButtonEvent((Button) mainView.findViewById(R.id.help_use4), 3);
        setButtonEvent((Button) mainView.findViewById(R.id.help_use5), 4);
        setButtonEvent((Button) mainView.findViewById(R.id.help_use6), 5);
        return mainView;
    }
    public void setButtonEvent(Button button, final int position) {
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setDisplay(position);
            }
        });
    }

    public void setDisplay(int position) {
        Fragment fragment = new TextFragment();
        Bundle args = new Bundle();
        displayScreen = position;
        switch (position) {
            case 0:
                args.putString("param1", getResources().getString(R.string.help_use1));
                break;
            case 1:
                args.putString("param1", getResources().getString(R.string.help_use2));
                break;
            case 2:
                args.putString("param1", getResources().getString(R.string.help_use3));
                break;
            case 3:
                args.putString("param1", getResources().getString(R.string.help_use4));
                break;
            case 4:
                args.putString("param1", getResources().getString(R.string.help_use5));
                break;
            case 5:
                args.putString("param1", getResources().getString(R.string.help_use6));
                break;
            default:
                break;
        }
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.help_frame_container, fragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("display", displayScreen);
        super.onSaveInstanceState(savedInstanceState);
    }
}
