package com.timitoc.groupic.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Global;

/**
 * Created by Cornel on 24.07.2016.
 */

public class OptionsFragment extends Fragment {
    private View mainView;
    private CheckBox autologin, confirmsave, confirmremove;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_options, container, false);

        autologin = (CheckBox) mainView.findViewById(R.id.options_autologin);
        autologin.setChecked(Global.want_login);
        autologin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoLoginChanged();
            }
        });

        confirmsave = (CheckBox) mainView.findViewById(R.id.options_confirmsave);
        confirmsave.setChecked(Global.isConfirm_save_image_on_local());
        confirmsave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                confirmSaveChanged(isChecked);
            }
        });

        confirmremove = (CheckBox) mainView.findViewById(R.id.options_confirmremove);
        confirmremove.setChecked(Global.isConfirm_delete_image_in_local());
        confirmremove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                confirmRemoveChanged(isChecked);
            }
        });

        return mainView;
    }

    private void autoLoginChanged(){
        Global.want_login = ((CheckBox)mainView.findViewById(R.id.options_autologin)).isChecked();
        SharedPreferences sharedPref = Global.getSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("want_login", Global.want_login);
        editor.commit();
    }

    private void confirmSaveChanged(boolean isChecked){
        Global.setConfirm_save_image_on_local(isChecked);
    }

    private void confirmRemoveChanged(boolean isChecked){
        Global.setConfirm_delete_image_in_local(isChecked);
    }

}