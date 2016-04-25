package com.timitoc.groupic.models;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.timitoc.groupic.activities.MainActivity;
import com.timitoc.groupic.R;

/**
 * Created by timi on 25.04.2016.
 */
public class FragmentLogin extends Fragment {
    View mainView;
    Button loginRequest;
    TextView loginAttemptResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_login, container, false);
        loginRequest = (Button) mainView.findViewById(R.id.login_request);
        loginAttemptResponse = (TextView) mainView.findViewById(R.id.login_attempt_response);
        loginRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAttempt();
            }
        });
        return mainView;
    }

    private boolean correctCredentials() {
        return false;
    }

    public void loginAttempt() {
        if (correctCredentials()) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            loginAttemptResponse.setText("Invalid username, password combination");
        }
    }



}
