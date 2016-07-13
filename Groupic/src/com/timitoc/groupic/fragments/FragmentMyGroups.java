package com.timitoc.groupic.fragments;

/**
 * Created by timi on 21.04.2016.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.adapters.NavDrawerListAdapter;
import com.timitoc.groupic.dialogBoxes.DeleteGroupDialogBox;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        try {
            searchServerForMyGroups(groupItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new MyGroupsListAdapter(getActivity(),
                groupItems);
        groupItemListView = (ListView) mainView.findViewById(R.id.list_my_groups);
        groupItemListView.setAdapter(adapter);
        groupItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GroupItem item = (GroupItem) adapterView.getItemAtPosition(i);
                if (!Global.deleteIconIsPressed)
                    enterGroupView(item);
                else
                    promptDeleteGroup(item);

            }
        });
    }

    private void promptDeleteGroup(GroupItem item) {
        new DeleteGroupDialogBox(){
            @Override
            public void leave() {

            }

            @Override
            public void cancel() {

            }
        }.show(getFragmentManager(), "4");
    }

    private void enterGroupView(GroupItem item) {
        Fragment fragment = GroupManageFragment.newInstance(item);
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        else
            Log.e("MainActivity", "Error in creating fragment");
    }

    public void searchServerForMyGroups(final ArrayList<GroupItem> groupItems) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        JSONObject jsonObject = new JSONObject();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("function", "get_groups_from_id");
        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        JSONObject params = new JSONObject();
        params.put("id", Global.user_id);
        String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        builder.appendQueryParameter("data", params.toString());
        builder.appendQueryParameter("hash", hash);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, builder.build().toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            System.out.println(response.getString("status"));
                            if ("success".equals(response.getString("status"))) {
                                JSONArray arr = response.getJSONArray("groups");
                                System.out.println("Response array size: " + arr.length());
                                for(int i=0; i < arr.length(); i++) {
                                    System.out.println(arr.getJSONObject(i).getString("title"));
                                    groupItems.add(new GroupItem(arr.getJSONObject(i)));
                                }
                            }
                            groupItemListView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());

            }
        });
        System.out.println(jsonRequest.getUrl());
        queue.add(jsonRequest);
    }

}
