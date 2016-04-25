package com.timitoc.groupic.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.FragmentGroups;
import com.timitoc.groupic.models.FragmentLogin;
import com.timitoc.groupic.models.FragmentRegister;
import com.timitoc.groupic.models.FragmentSecond;

/**
 * Created by timi on 25.04.2016.
 */
public class LoginActivity extends Activity {

    Button chooseLog, chooseRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        chooseLog = (Button) findViewById(R.id.login_choose);
        chooseRegister = (Button) findViewById(R.id.register_choose);
        chooseLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayView(0);
            }
        });
        chooseRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayView(1);
            }
        });

        displayView(0);
    }

    void displayView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new FragmentLogin();
                break;
            case 1:
                fragment = new FragmentRegister();
                break;
            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.form_frame_container, fragment).commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }
}
