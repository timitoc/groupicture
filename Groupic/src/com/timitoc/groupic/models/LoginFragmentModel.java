package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by timi on 28.04.2016.
 */
public class LoginFragmentModel implements Serializable{

    private String username, password;
    private boolean checked;

    public LoginFragmentModel() {
        username = "";
        password = "";
        checked = false;
    }

    public LoginFragmentModel(String username, String password, boolean checked) {
        this.username = username;
        this.password = password;
        this.checked = checked;
    }

    public boolean isEmpty() {
        return username.isEmpty() && password.isEmpty() && checked == false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
