package com.timitoc.groupic.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.activities.MainActivity;
import com.timitoc.groupic.R;
import com.timitoc.groupic.dialogBoxes.CreateFolderDialogBox;
import com.timitoc.groupic.dialogBoxes.ProposeOfflineUseDialogBox;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.SaveLocalManager;
import com.timitoc.groupic.utils.interfaces.Consumer;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timi on 25.04.2016.
 */
public class FragmentLogin extends Fragment {
    View mainView;
    Button loginRequest;
    TextView loginAttemptResponse;
    LoginFragmentModel model;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mainView = inflater.inflate(R.layout.fragment_login, container, false);
        loginRequest = (Button) mainView.findViewById(R.id.login_request);
        loginAttemptResponse = (TextView) mainView.findViewById(R.id.login_attempt_response);
        loginRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginAttempt();
            }
        });
        System.out.println("Called create with instance " + (savedInstanceState != null));
        if (getArguments() != null && getArguments().containsKey("login-model"))
            model = (LoginFragmentModel) this.getArguments().getSerializable("login-model");
        if (model == null || model.isEmpty())
            tryComplete();
        else {
            System.out.println(model.getUsername() + " " + model.getPassword() + " " + model.isChecked());
            useModel();
        }
       // }
        return mainView;
    }

    public void setModel(LoginFragmentModel model) {
        this.model = model;
    }

    private void useModel() {
        ((EditText)mainView.findViewById(R.id.username_textbox)).setText(model.getUsername());
        ((EditText)mainView.findViewById(R.id.password_textbox)).setText(model.getPassword());
        ((CheckBox)mainView.findViewById(R.id.auto_login_checkbox)).setChecked(model.isChecked());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        System.out.println("Called save");
        savedInstanceState.putString("login-username", ((EditText)mainView.findViewById(R.id.username_textbox)).getText().toString());
        savedInstanceState.putSerializable("login-model", model);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void tryComplete() {
        SharedPreferences sharedPref = Global.getSharedPreferences(getActivity());
        //System.out.println(sharedPref.getStringSet(SaveLocalManager.PREFERENCE_TAG, null).toString());
        if (Global.logging_out){
            Global.logging_out = false;
            Global.want_login = false;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("want_login", Global.want_login);
            editor.commit();
            return;
        }
        Global.want_login = sharedPref.getBoolean("want_login", false);

        ((CheckBox)mainView.findViewById(R.id.auto_login_checkbox)).setChecked(Global.want_login);
        String username = sharedPref.getString("username", "");
        String password = sharedPref.getString("password", "");
        if (!Global.want_login){
            return;
        }
        if (!username.isEmpty() && !password.isEmpty()) {
            ((EditText)mainView.findViewById(R.id.username_textbox)).setText(username);
            ((EditText)mainView.findViewById(R.id.password_textbox)).setText(password);
            loginAttempt();
        }

    }

    private void correctCredentials(final String username, final String password, final Consumer<String> consumer) throws JSONException {
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
                                consumer.accept("success");
                            }
                            else {
                                consumer.accept("invalid");
                                System.out.println("Invalid username password combination");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            consumer.accept("error_json");
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());
                consumer.accept("error_volley");
            }
        });
        System.out.println(jsonRequest.getUrl());
        queue.add(jsonRequest);
    }

    @Override
    public void onPause() {
        System.out.println("Login paused");
        System.out.println(model.getUsername() + " " + model.getPassword() + " " + model.isChecked());
        model.setUsername(((EditText)mainView.findViewById(R.id.username_textbox)).getText().toString());
        model.setPassword(((EditText)mainView.findViewById(R.id.password_textbox)).getText().toString());
        model.setChecked(((CheckBox)mainView.findViewById(R.id.auto_login_checkbox)).isChecked());
        System.out.println(model.getUsername() + " " + model.getPassword() + " " + model.isChecked());
        super.onPause();
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

    private void startMainActivity() {
        Global.initializeSettings(getActivity());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    public void loginAttempt() {
        loginAttemptResponse.setText("Authenticating");
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox)).getText().toString();
        try {
            correctCredentials(username, password, new Consumer<String>(){
                @Override
                public void accept(String status) {
                    if ("success".equals(status)) {
                        // auto log in?
                        Global.want_login = ((CheckBox)mainView.findViewById(R.id.auto_login_checkbox)).isChecked();

                        loginAttemptResponse.setText("Login succeeded");
                        SharedPreferences sharedPref = Global.getSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putBoolean("want_login", Global.want_login);
                        editor.commit();
                        ConnectionStateManager.setUsingState(ConnectionStateManager.UsingState.ONLINE);
                        startMainActivity();
                    }
                    else if ("invalid".equals(status)){
                        loginAttemptResponse.setText("Invalid username or password");
                        createProposeOfflineDialog();
                    }
                    else if (status.startsWith("error")) {
                        loginAttemptResponse.setText("Network error, are you connected to the internet?");
                        createProposeOfflineDialog();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            loginAttemptResponse.setText("An error occurred, please try again later");
        }
    }

    private void createProposeOfflineDialog() {
        new ProposeOfflineUseDialogBox() {
            @Override
            public void goOffline() {
                Global.user_id = -1;
                ConnectionStateManager.setUsingState(ConnectionStateManager.UsingState.OFFLINE);
                startMainActivity();
            }
        }.show(getFragmentManager(), "5");
    }

}
