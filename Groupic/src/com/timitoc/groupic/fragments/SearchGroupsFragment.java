package com.timitoc.groupic.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.models.SearchGroupsFragmentModel;
import com.timitoc.groupic.utils.*;
import com.timitoc.groupic.utils.interfaces.GroupEnterCallback;
import com.timitoc.groupic.utils.interfaces.ServerStatusCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by timi on 02.05.2016.
 */
public class SearchGroupsFragment extends Fragment {

    public static final int PAGE_SIZE = 10;

    View mainView;
    SearchView searchView;
    ListView foundGroups;
    MyGroupsListAdapter adapter;
    GroupItem selectedItem;
    SearchGroupsFragmentModel model;
    ArrayList<GroupItem> loadedGroupItems;

    private int currentPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.search_groups_fragment, container, false);
        searchView = (SearchView)mainView.findViewById(R.id.groups_search_bar);
        foundGroups = (ListView)mainView.findViewById(R.id.list_found_groups);
        if (getArguments() != null && getArguments().containsKey("search-model"))
            model = (SearchGroupsFragmentModel) getArguments().getSerializable("search-model");
        prepare();
        populate();
        return  mainView;
    }

    @Override
    public void onPause() {
        String query = searchView.getQuery().toString();
        model.setQuery(query);
        model.setGroupItems(loadedGroupItems);
        super.onPause();
    }

    void populate() {
        searchView.setQuery(model.getQuery(), false);
        loadedGroupItems = model.getGroupItems();
        adapter = new MyGroupsListAdapter(getActivity(), model.getGroupItems(), createGroupEnterEvent());
        foundGroups.setAdapter(adapter);
    }

    void prepare() {
        foundGroups.setOnScrollListener(EndlessScrollListener.getInstance(this));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("Start searching with query " + searchView.getQuery());
                searchForFirstGroups();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void searchForNextGroups(int page) {
        System.out.println("want page " + page);
        if (adapter == null)
            return;
        currentPage = page;
        try {
            getGroupsFromServer(loadedGroupItems, currentPage*PAGE_SIZE);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    private void searchForFirstGroups() {
        currentPage = 0;
        loadedGroupItems = new ArrayList<>();
        if (ConnectionStateManager.getUsingState() != ConnectionStateManager.UsingState.OFFLINE)
            adapter = new MyGroupsListAdapter(getActivity(), loadedGroupItems, createGroupEnterEvent());
        else
            adapter = new MyGroupsListAdapter(getActivity(), loadedGroupItems);
        foundGroups.setAdapter(adapter);
        EndlessScrollListener.getInstance(null).reset();

    }

    private boolean invalid(String data)
    {
        for (int i = 0; i < data.length(); i++)
            if (!Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i)) && !(data.charAt(i) == ' ')){
                String error = data.charAt(i) + " is not allowed in group password.";
                Toast.makeText(this.getActivity(), error, Toast.LENGTH_SHORT).show();
                return true;
            }
        return false;
    }

    private GroupEnterCallback createGroupEnterEvent() {
        return new GroupEnterCallback() {
            @Override
            public void call(GroupItem groupItem, @Nullable String input, TextView feedbackInfo) {
                try {
                    if (input == null) {
                        CreateNewGroupFragment.mapGroupToUser(groupItem.getId(), getActivity(), "", createFeedback(feedbackInfo));
                        return;
                    }
                    System.out.println("The input password is " + input + " for group " + groupItem.getTitle());
                    if (!invalid(input))
                        CreateNewGroupFragment.mapGroupToUser(groupItem.getId(), getActivity(), input, createFeedback(feedbackInfo));

                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Are you connected to the internet", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private ServerStatusCallback createFeedback(final TextView feedbackInfo) {
        return new ServerStatusCallback() {
            @Override
            public void onStatus(String status, String detail) {
                if ("success".equals(status))
                    feedbackInfo.setText("Successfully entered the group");
                else {
                    System.out.println("detail: " + detail);
                    if ("incorrect_password".equals(detail))
                        feedbackInfo.setText("Incorrect group password");
                    else if (detail.startsWith("Duplicate"))
                        feedbackInfo.setText("You are already part of this group");
                }
            }
        };
    }

    void getGroupsFromServer(final ArrayList<GroupItem> groupItems, int offset) throws JSONException {
        String url = Global.API_SERVICE_URL;
        final JSONObject params = new JSONObject();
        params.put("query", searchView.getQuery());
        params.put("offset", offset);
        params.put("size", PAGE_SIZE);
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("detail"))
                                System.out.println("jsonResponse detail " + jsonResponse.get("detail"));
                            if ("success".equals(jsonResponse.getString("status"))) {
                                JSONArray arr = jsonResponse.getJSONArray("groups");
                                System.out.println("Response array size: " + arr.length());
                                if (arr.length() == 0){
                                    if (getActivity() != null && isAdded())
                                    Toast.makeText(getActivity(), "No more results", Toast.LENGTH_SHORT).show();
                                }
                                for(int i=0; i < arr.length(); i++) {
                                    groupItems.add(new GroupItem(arr.getJSONObject(i)));
                                }
                                adapter.notifyDataSetChanged();
                                ConnectionStateManager.increaseUsingState();
                            }
                            else
                                noticeNetworkError();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            noticeNetworkError();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                        noticeNetworkError();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "search_for_groups");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(strRequest);
    }

    private void noticeNetworkError()
    {
        if (getActivity() != null && isAdded())
            Toast.makeText(getActivity(), "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
        ConnectionStateManager.decreaseUsingState();
    }
}
