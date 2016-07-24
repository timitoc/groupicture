package com.timitoc.groupic.models;

import java.io.Serializable;

/**
 * Created by timi on 24.07.2016.
 */
public class CreateNewGroupFragmentModel implements Serializable{

    private String name, description, password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
