package com.timitoc.groupic.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
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
public class SearchGroupsFragment extends Fragment{
    View mainView;
    SearchView searchView;
    ListView foundGroups;
    MyGroupsListAdapter adapter;
    GroupItem selectedItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.search_groups_fragment, container, false);
        searchView = (SearchView)mainView.findViewById(R.id.groups_search_bar);
        foundGroups = (ListView)mainView.findViewById(R.id.list_found_groups);
        prepare();
        return  mainView;
    }

    void prepare() {
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                System.out.println("Start searching with query " + searchView.getQuery());
                searchForGroups();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    void searchForGroups() {
        ArrayList<GroupItem> groupItems = new ArrayList<>();
        try {
            getGroupsFromServer(groupItems);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        if (ConnectionStateManager.getUsingState() != ConnectionStateManager.UsingState.OFFLINE)
            adapter = new MyGroupsListAdapter(getActivity(), groupItems, createGroupEnterEvent());
        else
            adapter = new MyGroupsListAdapter(getActivity(), groupItems);
        foundGroups.setAdapter(adapter);
        /*foundGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = (GroupItem) adapterView.getItemAtPosition(i);
                View summary;
                Button enterButton;
                EditText inputText = null;
                if (!selectedItem.hasPassword()) {
                    summary = view.findViewById(R.id.no_pass_layout);
                    enterButton = (Button) summary.findViewById(R.id.no_pass_enter);
                }
                else {
                    summary = view.findViewById(R.id.has_pass_layout);
                    enterButton = (Button) summary.findViewById(R.id.has_pass_enter);
                    inputText = (EditText) summary.findViewById(R.id.has_pass_input);
                }
                //enterButton.setOnClickListener(createGroupEnterEvent(selectedItem.hasPassword(), inputText));
                System.out.println("Calling toggle");
                ViewUtils.toggle(summary);
                //new ConfirmGroupEnteringDialog().show(getFragmentManager(), "3");
            }
        });*/
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
                    CreateNewGroupFragment.mapGroupToUser(groupItem.getId(), getActivity(), input, createFeedback(feedbackInfo));

                    //new ConfirmGroupEnteringDialog().show(getFragmentManager(), "3");
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

    void getGroupsFromServer(final ArrayList<GroupItem> groupItems) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("query", searchView.getQuery());
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            System.out.println("jsonResponse status " + jsonResponse.getString("status"));
                            if (jsonResponse.has("detail"))
                                System.out.println("jsonResponse detail " + jsonResponse.get("detail"));
                            if ("success".equals(jsonResponse.getString("status"))) {
                                System.out.println("Successfully loaded groups");
                                JSONArray arr = jsonResponse.getJSONArray("groups");
                                System.out.println("Response array size: " + arr.length());
                                for(int i=0; i < arr.length(); i++) {
                                    System.out.println(arr.getJSONObject(i).getString("title"));
                                    groupItems.add(new GroupItem(arr.getJSONObject(i)));
                                }
                                foundGroups.setAdapter(adapter);
                            }
                            else
                                Toast.makeText(getActivity(), "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                        Toast.makeText(getActivity(), "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
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

        queue.add(strRequest);
    }


    /// Fragment out-of-use, delete if stick to plan A.
    @SuppressLint("ValidFragment")
    private class ConfirmGroupEnteringDialog extends DialogFragment {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Do you want to enter this Group?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("Fire");
                                try {
                                    CreateNewGroupFragment.mapGroupToUser(selectedItem.getId(), getActivity(), null);
                                    Toast.makeText(getActivity(), "Group entered successfully", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "Are you connected to the internet", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("Don't fire");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();

            }
    }
}
