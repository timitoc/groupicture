package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.timitoc.groupic.R;
import com.timitoc.groupic.activities.MainActivity;
import com.timitoc.groupic.dialogBoxes.ProposeOfflineUseDialogBox;
import com.timitoc.groupic.models.LoginFragmentModel;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.VolleySingleton;
import com.timitoc.groupic.utils.interfaces.Consumer;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timi on 25.04.2016.
 */
public class LoginFragment extends Fragment {
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
        if (getArguments() != null && getArguments().containsKey("login-model"))
            model = (LoginFragmentModel) this.getArguments().getSerializable("login-model");
        if (model == null || model.isEmpty())
            tryComplete();
        else {
            useModel();
        }
        return mainView;
    }

    private void useModel() {
        ((EditText)mainView.findViewById(R.id.username_textbox)).setText(model.getUsername());
        ((EditText)mainView.findViewById(R.id.password_textbox)).setText(model.getPassword());
        ((CheckBox)mainView.findViewById(R.id.auto_login_checkbox)).setChecked(model.isChecked());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("login-username", ((EditText)mainView.findViewById(R.id.username_textbox)).getText().toString());
        savedInstanceState.putSerializable("login-model", model);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void tryComplete() {
        SharedPreferences sharedPref = Global.getSharedPreferences(getActivity());
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
        String url = getString(R.string.api_service_url);
        JSONObject jsonObject = new JSONObject();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("function", "login_user");
        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        JSONObject params = new JSONObject();
        params.put("password", Encryptor.hash(password));
        params.put("username", username);



        String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        builder.appendQueryParameter("data", params.toString());
        builder.appendQueryParameter("hash", hash);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, builder.build().toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if ("success".equals(response.getString("status"))) {
                                Global.user_id = response.getInt("id");
                                consumer.accept("success");
                            }
                            else {
                                consumer.accept("invalid");
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
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonRequest);
    }

    @Override
    public void onPause() {
        model.setUsername(((EditText)mainView.findViewById(R.id.username_textbox)).getText().toString());
        model.setPassword(((EditText)mainView.findViewById(R.id.password_textbox)).getText().toString());
        model.setChecked(((CheckBox)mainView.findViewById(R.id.auto_login_checkbox)).isChecked());
        super.onPause();
    }

    private void startMainActivity() {
        Global.initializeSettings(getActivity());
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private String[] aux = {"username", "password"};
    private boolean invalid(String data, int str)
    {
        for (int i = 0; i < data.length(); i++)
            if (!Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i)) && !(data.charAt(i) == ' ')){
                String error = data.charAt(i) + " is not allowed in " + aux[str] + ".";
                loginAttemptResponse.setText(error);
                return true;
            }
        return false;
    }

    public void loginAttempt() {
        loginAttemptResponse.setText("Authenticating");
        final String username = ((TextView)mainView.findViewById(R.id.username_textbox)).getText().toString();
        final String password = ((TextView)mainView.findViewById(R.id.password_textbox)).getText().toString();
        if (!invalid(username, 0) && !invalid(password, 1)) {
            try {
                correctCredentials(username, password, new Consumer<String>() {
                    @Override
                    public void accept(String status) {
                        if ("success".equals(status)) {
                            Global.want_login = ((CheckBox) mainView.findViewById(R.id.auto_login_checkbox)).isChecked();
                            loginAttemptResponse.setText("Login succeeded");
                            SharedPreferences sharedPref = Global.getSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", username);
                            editor.putString("password", password);
                            editor.putBoolean("want_login", Global.want_login);
                            editor.commit();
                            Global.user_username = username;
                            Global.user_password = password;
                            ConnectionStateManager.setUsingState(ConnectionStateManager.UsingState.ONLINE);
                            startMainActivity();
                        } else if ("invalid".equals(status)) {
                            loginAttemptResponse.setText("Invalid username or password");
                            createProposeOfflineDialog();
                        } else if (status.startsWith("error")) {
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
