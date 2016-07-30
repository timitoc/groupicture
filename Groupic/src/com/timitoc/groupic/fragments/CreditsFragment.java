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
 * Created by Cornel on 25.07.2016.
 */
public class CreditsFragment extends Fragment{
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView =  inflater.inflate(R.layout.fragment_credits, container, false);
        setDisplay(0);
        Global.onRefreshMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                setDisplay(0);
            }
        };
        setButtonEvent((Button) mainView.findViewById(R.id.credits1), 0);
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
        switch (position) {
            case 0:
                args.putString("param1", getResources().getString(R.string.credits1));
                break;
            default:
                break;
        }
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.credits_frame_container, fragment).commit();
    }
}