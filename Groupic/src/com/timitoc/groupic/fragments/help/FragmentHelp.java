package com.timitoc.groupic.fragments.help;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Global;

/**
 * Created by Cornel on 02.05.2016.
 */
public class FragmentHelp extends Fragment{
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_help, container, false);
        setDisplay(0);
        Global.onRefreshMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                setDisplay(0);
            }
        };
        setButtonEvent((Button) mainView.findViewById(R.id.help_use), 0);
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
                fragment = new Fragment2HelpUse();
                System.out.println("0");
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.help_frame_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("frgamentabout", "Error in creating fragment");
        }
    }
}
