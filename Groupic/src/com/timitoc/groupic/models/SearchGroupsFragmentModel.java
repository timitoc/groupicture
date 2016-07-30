package com.timitoc.groupic.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by timi on 24.07.2016.
 */
public class SearchGroupsFragmentModel implements Serializable {
    private String query;
    private ArrayList<GroupItem> groupItems;

    public SearchGroupsFragmentModel() {
        query = "";
        groupItems = new ArrayList<>();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public ArrayList<GroupItem> getGroupItems() {
        return groupItems;
    }

    public void setGroupItems(ArrayList<GroupItem> groupItems) {
        this.groupItems = groupItems;
    }
}
