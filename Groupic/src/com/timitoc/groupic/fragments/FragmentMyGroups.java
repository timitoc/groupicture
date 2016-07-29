package com.timitoc.groupic.fragments;

/**
 * Created by timi on 21.04.2016.
 */


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.activities.LoginActivity;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.dialogBoxes.DeleteGroupDialogBox;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.utils.*;
import com.timitoc.groupic.utils.interfaces.Consumer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FragmentMyGroups extends Fragment {

    MyGroupsListAdapter adapter;
    ListView groupItemListView;
    View mainView;

    public FragmentMyGroups() {
        //this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_my_groups, container, false);
        System.out.println("Creating view");
        prepare();

        return  mainView;
    }

    public void prepare() {
        ArrayList<GroupItem> groupItems = new ArrayList<>();

        adapter = new MyGroupsListAdapter(getActivity(),
                groupItems);
        groupItemListView = (ListView) mainView.findViewById(R.id.list_my_groups);
        groupItemListView.setAdapter(adapter);
        try {
            searchServerForMyGroups(groupItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void promptDeleteGroup(final GroupItem item) {
        DeleteGroupDialogBox.newInstance(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean) {
                    try {
                        unmapGroupFromUser(item);
                        if (getActivity() != null && isAdded())
                            Toast.makeText(getActivity(), "Successfully left group", Toast.LENGTH_SHORT).show();
                        Global.onRefreshMenuItemClicked.run();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("Exception rised");
                    }
                }

            }
        }).show(getChildFragmentManager(), "4");
    }


    private void enterGroupView(GroupItem item) {
        Fragment fragment = GroupManageFragment.newInstance(item);
        if (fragment != null) {
            FragmentManager fragmentManager = this.getParentFragment().getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        else
            Log.e("MainActivity", "Error in creating fragment");
    }

    private void unmapGroupFromUser(GroupItem groupItem) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(Global.baseActivity);
        String url = Global.API_SERVICE_URL;
        //JSONObject jsonObject = new JSONObject();
        Map<String, String> map = new HashMap<String, String>();
        map.put("function", "unmap_group_from_user");
        map.put("public_key", Global.MY_PUBLIC_KEY);
        JSONObject params = new JSONObject();
        params.put("user_id", Global.user_id);
        params.put("group_id", groupItem.getId());
        String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        map.put("data", params.toString());
        map.put("hash", hash);

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, map, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    System.out.println(("That didn't work!\n") + error.getMessage());
                }
        });
        System.out.println(customRequest.getUrl());
        queue.add(customRequest);

    }

    public void searchServerForMyGroups(final ArrayList<GroupItem> groupItems) throws JSONException {
        if (ConnectionStateManager.getUsingState() == ConnectionStateManager.UsingState.OFFLINE) {
            loadLocalSaves(groupItems);
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        JSONObject jsonObject = new JSONObject();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("function", "get_groups_from_id");
        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        JSONObject params = new JSONObject();
        params.put("id", Global.user_id);
        params.put("username", Global.user_username);
        params.put("password", Encryptor.hash(Global.user_password));
        String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        builder.appendQueryParameter("data", params.toString());
        builder.appendQueryParameter("hash", hash);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, builder.build().toString(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            System.out.println(status);
                            if ("success".equals(status)) {
                                JSONArray arr = response.getJSONArray("groups");
                                System.out.println("Response array size: " + arr.length());
                                for(int i=0; i < arr.length(); i++) {
                                    //System.out.println(arr.getJSONObject(i).getString("title"));
                                    groupItems.add(new GroupItem(arr.getJSONObject(i)));
                                }
                                ConnectionStateManager.increaseUsingState();
                                groupItemListView.setAdapter(adapter);
                            }
                            else {
                                if ("failure".equals(status) && "error_auth_user_credentials".equals(response.getString("detail"))) {
                                    System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRROOOOOOOOOOOOOOOOOOOOORRRR");
                                    if (getActivity() != null && isAdded()) {
                                        Toast.makeText(getActivity(), "Authentication error", Toast.LENGTH_SHORT).show();
                                        logout();
                                    }

                                }
                                loadLocalSaves(groupItems);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loadLocalSaves(groupItems);
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());
                loadLocalSaves(groupItems);
            }
        });
        System.out.println(jsonRequest.getUrl());
        queue.add(jsonRequest);
    }

    private void logout() {
        Global.want_login = false;
        Global.logging_out = true;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void loadLocalSaves(ArrayList<GroupItem> groupItems) {
        ConnectionStateManager.decreaseUsingState();
        Set<String> groupSet = SaveLocalManager.getGroupsSet();
        for (String groupString : groupSet) {
            String[] fields = groupString.split("#");
            int id = Integer.parseInt(fields[1], 16);
            String title = fields[2];
            String description = fields[3];
            GroupItem groupItem= new GroupItem(id, title, description);
            groupItems.add(groupItem);
        }

        groupItemListView.setAdapter(adapter);
    }


}
