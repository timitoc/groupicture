package com.timitoc.groupic.fragments.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.timitoc.groupic.R;

/**
 * Created by Cornel on 02.05.2016.
 */
public class Fragment2Help extends android.app.Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View mainView = inflater.inflate(R.layout.fragment2_help, container, false);
            TextView textView = (TextView)mainView.findViewById(R.id.textForHelp);
            textView.setText(getArguments().getString("param1"));
            return mainView;
        }
}
