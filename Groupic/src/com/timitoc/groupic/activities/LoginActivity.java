package com.timitoc.groupic.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.fragments.FragmentLogin;
import com.timitoc.groupic.fragments.FragmentRegister;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.models.RegisterFragmentModel;
import com.timitoc.groupic.utils.Global;

/**
 * Created by timi on 25.04.2016.
 */
public class LoginActivity extends Activity {

    Button chooseLog, chooseRegister;
    FragmentLogin loginFragment;
    FragmentRegister registerFragment;
    LoginFragmentModel loginModel;
    RegisterFragmentModel registerModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);
        Global.API_SERVICE_URL = getString(R.string.api_service_url);
        /*if (savedInstanceState != null) {
            if (loginFragment == null)
                loginFragment = getFragmentManager().getFragment(savedInstanceState, "login-fragment");
            if (registerFragment == null)
                registerFragment = getFragmentManager().getFragment(savedInstanceState, "register-fragment");
        }
        else
            displayView(0);*/

        if (savedInstanceState == null) {
            loginModel = new LoginFragmentModel();
            registerModel = new RegisterFragmentModel();
        }
        else {
            loginModel = (LoginFragmentModel) savedInstanceState.getSerializable("login-model");
            registerModel = (RegisterFragmentModel) savedInstanceState.getSerializable("register-model");
        }
        if (savedInstanceState == null)
            displayView(0);

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

    }

    void displayView(int position) {
        Fragment fragment = null;
        System.out.println("Displaying fragment");
        Bundle args;
        switch (position) {
            case 0:
                loginFragment = new FragmentLogin();
                args = new Bundle();
                args.putSerializable("login-model", loginModel);
                loginFragment.setArguments(args);
                fragment = loginFragment;
                break;
            case 1:
                registerFragment = new FragmentRegister();
                args = new Bundle();
                args.putSerializable("register-model", registerModel);
                registerFragment.setArguments(args);
                fragment = registerFragment;
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


    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        /*if (loginFragment != null && loginFragment.isAdded())
            getFragmentManager().putFragment(savedInstanceState, "login-fragment", loginFragment);
        if (registerFragment != null && registerFragment.isAdded())
            getFragmentManager().putFragment(savedInstanceState, "register-fragment", registerFragment);*/
        savedInstanceState.putSerializable("login-model", loginModel);
        savedInstanceState.putSerializable("register-model", registerModel);
    }
}
