package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class GroupItem implements Serializable {

    private String title;
    private String description;
    private int id;
    private boolean hasPassword;

    public GroupItem(){

    }

    public GroupItem(int id, String title, String description){
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public GroupItem(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("title");
            this.description = jsonObject.getString("description");
            this.id = jsonObject.getInt("id");
            if (jsonObject.has("hasPassword"))
                this.hasPassword = jsonObject.getBoolean("hasPassword");
            else
                this.hasPassword = false;
        } catch (JSONException e) {
            e.printStackTrace();
            this.id = 0;
            this.title = "nil";
            this.description = "nil";
            this.hasPassword = false;
        }

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean hasPassword() {
        return hasPassword;
    }
}