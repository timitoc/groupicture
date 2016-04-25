package com.timitoc.groupic.models;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.timitoc.groupic.R;

/**
 * Created by timi on 25.04.2016.
 */
public class FragmentRegister extends Fragment {
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_register, container, false);
        return mainView;
    }

}