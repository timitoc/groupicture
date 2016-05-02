package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.timitoc.groupic.R;

/**
 * Created by timi on 02.05.2016.
 */
public class SearchGroupsFragment extends Fragment{
    View mainView;
    SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.search_groups_fragment, container, false);
        searchView = (SearchView)mainView.findViewById(R.id.groups_search_bar);
        prepare();
        return  mainView;
    }

    void prepare() {
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("Start searching with query " + searchView.getQuery());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}
