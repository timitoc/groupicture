package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

public class GroupItem {

    private String title;
    private String description;

    public GroupItem(){

    }

    public GroupItem(String title, String description){
        this.title = title;
        this.description = description;
    }

    public GroupItem(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("title");
            this.description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
            this.title = "nil";
            this.description = "nil";
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
}