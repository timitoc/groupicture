package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox_r)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox_r)).getText().toString();
        final String name = ((TextView)mainView.findViewById(R.id.name_textbox_r)).getText().toString();
        return username.length() > 0 && password.length() > 4 && name.length() > 0;
    }

    public void saveUserInDatabase() throws JSONException {
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox_r)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox_r)).getText().toString();
        final String name = ((TextView)mainView.findViewById(R.id.name_textbox_r)).getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("username", username);
        params.put("password", Encryptor.hash(password));
        params.put("name", name);
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if ("success".equals(jsonResponse.getString("status"))) {
                                System.out.println("success to register user");
                                SharedPreferences sharedPref = Global.getSharedPreferences(getActivity());
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.apply();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else {
                                System.out.println("Username is already taken");
                                registerAttemptResponse.setText("Username is already taken");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("exception to register user");
                            registerAttemptResponse.setText("Network error occurred please try again latter");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "register_user");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        queue.add(strRequest);

    }

    public void registerAttempt() {
        if (validCredentials()) {
            try {
                saveUserInDatabase();
            } catch (JSONException e) {
                e.printStackTrace();
                registerAttemptResponse.setText("Network error occurred please try again latter");
            }
        }
        else {
            registerAttemptResponse.setText("Invalid username, password combination");
        }
    }



}
