package com.timitoc.groupic.models;

import com.timitoc.groupic.fragments.CreateNewGroupFragment;

import java.io.Serializable;

/**
 * Created by timi on 24.07.2016.
 */
public class GroupsFragmentModel implements Serializable{
    public SearchGroupsFragmentModel searchModel;
    public CreateNewGroupFragmentModel createModel;

    public GroupsFragmentModel() {
        searchModel = new SearchGroupsFragmentModel();
        createModel = new CreateNewGroupFragmentModel();
    }

}
