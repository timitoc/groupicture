package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timi on 28.04.2016.
 */
public class FolderItem {
    private String title;
    private int id;

    public FolderItem(){

    }

    public FolderItem(int id, String title){
        this.id = id;
        this.title = title;
    }

    public FolderItem(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("title");
            this.id = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
            this.id = 0;
            this.title = "nil";
        }

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
