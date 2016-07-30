package com.timitoc.groupic.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by timi on 28.04.2016.
 */
public class FolderItem implements Serializable{
    private String title;
    private int id;
    private GroupItem parentGroup;
    private int color;

    public FolderItem(int id, String title, GroupItem parentGroup, int color){
        this.id = id;
        this.title = title;
        this.parentGroup = parentGroup;
        this.color = color;
    }

    public FolderItem(JSONObject jsonObject, GroupItem parentGroup) {
        try {
            this.title = jsonObject.getString("title");
            this.id = jsonObject.getInt("id");
            this.parentGroup = parentGroup;
        } catch (JSONException e) {
            e.printStackTrace();
            this.id = 0;
            this.title = "nil";
            this.parentGroup = null;
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


    public GroupItem getParentGroup() {
        return parentGroup;
    }

    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }
}
