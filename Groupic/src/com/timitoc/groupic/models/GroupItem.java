package com.timitoc.groupic.models;

public class GroupItem {

    private String title;
    private String description;

    public GroupItem(){

    }

    public GroupItem(String title, String description){
        this.title = title;
        this.description = description;
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