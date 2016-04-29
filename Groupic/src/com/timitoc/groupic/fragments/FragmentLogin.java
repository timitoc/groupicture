package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.activities.MainActivity;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Consumer;
import com.timitoc.groupic.utils.Global;
import org.json.JSONException;
import org.json.JSONObject;

import javax.microedition.khronos.opengles.GL;
import java.util.HashMap;
import java.util.Map;

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
        tryComplete();
        return mainView;
    }

    private void tryComplete() {
        if (!Global.want_login)
            return;
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        if (!username.isEmpty() && !password.isEmpty()) {
            ((TextView)mainView.findViewById(R.id.username_textbox)).setText(username);
            ((TextView)mainView.findViewById(R.id.password_textbox)).setText(password);
            loginAttempt();
        }

    }

    private void correctCredentials(final String username, final String password, final Consumer<Boolean> consumer) {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.auth_url);
        JSONObject jsonObject = new JSONObject();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("username", username);
        builder.appendQueryParameter("password", password);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, builder.build().toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response.getString("status"));
                            System.out.println("Requested username " + response.getString("username"));
                            System.out.println("Requested password " + response.getString("password"));
                            if ("succes".equals(response.getString("status"))) {
                                Global.user_id = response.getInt("id");
                                System.out.println("Id is " + Global.user_id);
                                consumer.accept(true);
                            }
                            else
                                consumer.accept(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            consumer.accept(false);
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<>();
                System.out.println("Do you even call");
                pars.put("username", username);
                pars.put("password", password);
                return pars;
            }
        };
        System.out.println(jsonRequest.getUrl());
        queue.add(jsonRequest);
    }

    public void loginAttempt() {
        loginAttemptResponse.setText("Authenticating");
        loginAttemptResponse.setTextColor(0xffff00);
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox)).getText().toString();
        correctCredentials(username, password, new Consumer<Boolean>(){
            @Override
            public void accept(Boolean h) {
                if (h) {
                    loginAttemptResponse.setText("Login succeeded");
                    loginAttemptResponse.setTextColor(0x00ff00);
                    SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else {
                    loginAttemptResponse.setText("Invalid username or password");
                }
            }
        });
    }



}
