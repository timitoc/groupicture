package com.timitoc.groupic.models;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.activities.MainActivity;
import com.timitoc.groupic.R;

import java.util.HashMap;
import java.util.Map;

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

    private boolean validCredentials() {
        return true;
    }

    public void saveUserInDatabase() {
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox_r)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox_r)).getText().toString();
        final String name = ((TextView)mainView.findViewById(R.id.name_textbox_r)).getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url ="http://192.168.1.52:8084/FirstNetBean/RegisterUser";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        System.out.println(("Response is: " + response));
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("username", username);
                pars.put("password", password);
                pars.put("name", name);
                return pars;
            }
        };
        System.out.println(stringRequest);
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void registerAttempt() {
        if (validCredentials()) {
            saveUserInDatabase();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        else {
            registerAttemptResponse.setText("Invalid username, password combination");
        }
    }



}
