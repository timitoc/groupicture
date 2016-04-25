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
public class FragmentRegister extends Fragment {
    View mainView;
    Button registerRequest;
    TextView registerAttemptResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_register, container, false);
        registerRequest = (Button) mainView.findViewById(R.id.register_request);
        registerAttemptResponse = (TextView) mainView.findViewById(R.id.register_attempt_response);
        registerRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAttempt();
            }
        });
        return mainView;
    }

    private boolean correctCredentials() {
        return true;
    }

    public void registerAttempt() {
        if (correctCredentials()) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            registerAttemptResponse.setText("Invalid username, password combination");
        }
    }



}
