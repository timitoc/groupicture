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
import android.widget.*;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.activities.MainActivity;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.models.RegisterFragmentModel;
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
public class RegisterFragment extends Fragment {
    View mainView;
    Button registerRequest;
    TextView registerAttemptResponse;
    RegisterFragmentModel model;

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
        if (getArguments() != null && getArguments().containsKey("register-model"))
            model = (RegisterFragmentModel) this.getArguments().getSerializable("register-model");
        useModel();
        return mainView;
    }

    public void setModel(RegisterFragmentModel model) {
        this.model = model;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable("register-model", model);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause() {
        model.setUsername(((EditText)mainView.findViewById(R.id.username_textbox_r)).getText().toString());
        model.setPassword(((EditText)mainView.findViewById(R.id.password_textbox_r)).getText().toString());
        model.setNickname(((EditText)mainView.findViewById(R.id.name_textbox_r)).getText().toString());

        System.out.println(model.getUsername() + " " + model.getPassword() + " " + model.getNickname());
        super.onPause();
    }

    private void useModel() {
        ((EditText)mainView.findViewById(R.id.username_textbox_r)).setText(model.getUsername());
        ((EditText)mainView.findViewById(R.id.password_textbox_r)).setText(model.getPassword());
        ((EditText)mainView.findViewById(R.id.name_textbox_r)).setText(model.getNickname());
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

    private String[] aux = {"username", "password", "nickname"};
    private boolean invalid(String data, int str) {
        for (int i = 0; i < data.length(); i++)
            if (!Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i)) && !(data.charAt(i) == ' ')){
                String error = data.charAt(i) + " is not allowed in " + aux[str] + ".";
                registerAttemptResponse.setText(error);
                return true;
            }
        return false;
    }

    private boolean tooShort(String data, int str){
        if (data.length() == 0){
            String error = aux[str] + " is too short";
            registerAttemptResponse.setText(error);
            return true;
        }
        return false;
    }

    private boolean validCredentials() {
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox_r)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox_r)).getText().toString();
        final String name = ((TextView)mainView.findViewById(R.id.name_textbox_r)).getText().toString();

        if (password.length() < 5){
            String error = "Password too short";
            registerAttemptResponse.setText(error);
            return false;
        }
        return !tooShort(username, 0) && !tooShort(name, 2) && !invalid(username, 0) && !invalid(password, 1) && !invalid(name, 2);
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
    }



}
