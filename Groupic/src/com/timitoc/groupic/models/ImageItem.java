package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by timi on 30.04.2016.
 */
public class ImageItem {
    private int id;
    private String title;
    private String requestUrl;

    public ImageItem(){

    }

    public ImageItem(int id, String title, String requestUrl){
        this.id = id;
        this.title = title;
        this.requestUrl = requestUrl;
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

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }
}
