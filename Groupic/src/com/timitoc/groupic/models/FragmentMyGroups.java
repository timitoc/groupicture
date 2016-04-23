package com.timitoc.groupic.models;

/**
 * Created by timi on 21.04.2016.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.adapters.NavDrawerListAdapter;

import java.util.ArrayList;

public class FragmentMyGroups extends Fragment{

    MyGroupsListAdapter adapter;
    ListView groupItemListView;
    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_my_groups, container, false);
        prepare();
        return  mainView;
    }

    public void prepare() {
        ArrayList<GroupItem> groupItems = new ArrayList<>();
        groupItems.add(new GroupItem("Test", ""));
        groupItems.add(new GroupItem("", "Merge"));
        groupItems.add(new GroupItem("Bun", ""));
        adapter = new MyGroupsListAdapter(getActivity(),
                groupItems);
        groupItemListView = (ListView) mainView.findViewById(R.id.list_my_groups);
        groupItemListView.setAdapter(adapter);
    }

}
