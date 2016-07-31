package com.timitoc.groupic.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.timitoc.groupic.R;
import com.timitoc.groupic.fragments.LoginFragment;
import com.timitoc.groupic.fragments.RegisterFragment;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.models.RegisterFragmentModel;
import com.timitoc.groupic.utils.EncryptorTest;
import com.timitoc.groupic.utils.Global;

/**
 * Created by timi on 25.04.2016.
 */
public class LoginActivity extends Activity {

    Button chooseLog, chooseRegister;
    LoginFragment loginFragment;
    RegisterFragment registerFragment;
    LoginFragmentModel loginModel;
    RegisterFragmentModel registerModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);
        Global.API_SERVICE_URL = getString(R.string.api_service_url);

        if (savedInstanceState == null) {
            loginModel = new LoginFragmentModel();
            registerModel = new RegisterFragmentModel();
        }
        else {
            loginModel = (LoginFragmentModel) savedInstanceState.getSerializable("login-model");
            registerModel = (RegisterFragmentModel) savedInstanceState.getSerializable("register-model");
        }
        disclaim();
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

    private void disclaim() {
        final SharedPreferences pref = this.getSharedPreferences(Global.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean accepted = pref.getBoolean("accepted", false);
        if (!accepted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.disclaimer_string)
                    .setCancelable(false)
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("accepted", true);
                            editor.commit();
                        }
                    })
                    .setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            System.exit(0);
                        }
                    }).show();
        }
    }

    void displayView(int position) {
        Fragment fragment = null;
        System.out.println("Displaying fragment");
        Bundle args;
        switch (position) {
            case 0:
                loginFragment = new LoginFragment();
                args = new Bundle();
                args.putSerializable("login-model", loginModel);
                loginFragment.setArguments(args);
                fragment = loginFragment;
                break;
            case 1:
                registerFragment = new RegisterFragment();
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
        savedInstanceState.putSerializable("login-model", loginModel);
        savedInstanceState.putSerializable("register-model", registerModel);
    }
}
