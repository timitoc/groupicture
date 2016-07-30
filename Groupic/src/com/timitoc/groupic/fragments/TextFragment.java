package com.timitoc.groupic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.timitoc.groupic.R;

/**
 * Created by Cornel on 02.05.2016.
 */
public class TextFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_text, container, false);
        TextView textView = (TextView)mainView.findViewById(R.id.textForHelp);
        textView.setText(getArguments().getString("param1"));
        return mainView;
    }
}
