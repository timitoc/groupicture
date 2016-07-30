package com.timitoc.groupic.models;

import java.io.Serializable;

/**
 * Created by timi on 28.04.2016.
 */
public class RegisterFragmentModel implements Serializable{

    private String username, password, nickname;

    public RegisterFragmentModel() {
        username = "";
        password = "";
        nickname = "";
    }

    public boolean isEmpty() {
        return username.isEmpty() && password.isEmpty() && nickname.isEmpty();
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
