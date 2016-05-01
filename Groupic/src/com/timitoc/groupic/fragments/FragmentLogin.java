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
import com.timitoc.groupic.models.FolderItem;
import com.timitoc.groupic.utils.Consumer;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONArray;
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

    private void correctCredentials(final String username, final String password, final Consumer<Boolean> consumer) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        JSONObject jsonObject = new JSONObject();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("function", "login_user");
        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        JSONObject params = new JSONObject();
        params.put("password", Encryptor.hash(password));
        params.put("username", username);



        String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        System.out.println("Hash str: " + params.toString() + Global.MY_PRIVATE_KEY);
        builder.appendQueryParameter("data", params.toString());
        builder.appendQueryParameter("hash", hash);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, builder.build().toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response.getString("status"));
                            if ("success".equals(response.getString("status"))) {
                                System.out.println("Success");
                                Global.user_id = response.getInt("id");
                                consumer.accept(true);
                            }
                            else {
                                consumer.accept(false);
                                System.out.println("Failure");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Error");
                            consumer.accept(false);
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());

            }
        });
        System.out.println(jsonRequest.getUrl());
        queue.add(jsonRequest);
    }


   /* private void correctCredentials(final String username, final String password, final Consumer<Boolean> consumer) throws JSONException {

        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("username", username);
        params.put("password", password);

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
                                System.out.println("success to login user");
                                consumer.accept(true);
                            }
                            else {
                                System.out.println("failed to login user with status " + jsonResponse.getString("status"));
                                System.out.println("failed to login user with detail " + jsonResponse.getString("detail"));
                                //System.out.println("Received username " + jsonResponse.getString("username"));
                                //System.out.println("Received password " + jsonResponse.getString("password"));
                                consumer.accept(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            consumer.accept(false);
                            System.out.println("exception to login user");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                        consumer.accept(false);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                System.out.println("want params");
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "login_user");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };
        System.out.println(strRequest.getUrl());
        queue.add(strRequest);
    }*/


    public void loginAttempt() {
        loginAttemptResponse.setText("Authenticating");
        loginAttemptResponse.setTextColor(0xffff00);
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox)).getText().toString();
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
            loginAttemptResponse.setText("Network error occurred, please try again later");
        }
    }



}
