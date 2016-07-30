package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by timi on 30.04.2016.
 */
public class ImageItem implements Serializable{
    private int id;
    private String title;
    private String requestUrl;
    private FolderItem parentFolder;

    public ImageItem(int id, String title, String requestUrl, FolderItem parentFolder){
        this.id = id;
        this.title = title;
        this.requestUrl = requestUrl;
        this.parentFolder = parentFolder;
    }

    public ImageItem(JSONObject jsonObject) {
        try {
            this.title = jsonObject.getString("title");
            this.id = jsonObject.getInt("id");
            this.requestUrl = jsonObject.getString("request-url");
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

    public String getRequestUrl() {
        return requestUrl;
    }

    public FolderItem getParentFolder() {
        return parentFolder;
    }
}
