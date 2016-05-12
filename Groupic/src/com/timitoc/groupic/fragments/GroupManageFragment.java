package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyFoldersListAdapter;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.models.AddNewDialogBox;
import com.timitoc.groupic.models.FolderItem;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by timi on 28.04.2016.
 * Fragment regarding Group view within folders
 */
public class GroupManageFragment extends Fragment {

    View mainView;
    GroupItem groupItem;
    MyFoldersListAdapter adapter;
    ListView folderItemListView;

    public static GroupManageFragment newInstance(GroupItem groupItem) {
        GroupManageFragment myFragment = new GroupManageFragment();

        Bundle args = new Bundle();
        args.putSerializable("group_item", groupItem);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.group_manage_fragment, container, false);
        groupItem =  (GroupItem) getArguments().getSerializable("group_item");
        Global.current_group_id = groupItem.getId();
        prepare();
        return mainView;
    }

    public void prepare() {
        Global.onAddMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                Global.current_folder_id = 0;
                new AddNewDialogBox().show(getFragmentManager(), "1");
            }
        };
        Global.onRefreshMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                prepare();
            }
        };
        ArrayList<FolderItem> folderItems = new ArrayList<>();
        try {
            searchServerForGroupFolders(folderItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter = new MyFoldersListAdapter(getActivity(),
                folderItems);
        folderItemListView = (ListView) mainView.findViewById(R.id.list_group_folders);
        folderItemListView.setAdapter(adapter);
        folderItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FolderItem item = (FolderItem) adapterView.getItemAtPosition(i);
                showFolderContent(item);

            }
        });
    }

    public void showFolderContent(FolderItem item) {
        System.out.println(item.getTitle() + " folder pressed ");
        Fragment fragment = FolderContentFragment.newInstance(item.getId());
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.folder_content, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void searchServerForGroupFolders(final ArrayList<FolderItem> folderItems) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        JSONObject jsonObject = new JSONObject();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("function", "get_folders_from_group");
        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        JSONObject params = new JSONObject();
        params.put("id", groupItem.getId()); /// id-ul grupului selectat
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
                                    folderItems.add(new FolderItem(arr.getJSONObject(i)));
                                }
                            }
                            folderItemListView.setAdapter(adapter);
                            if (!adapter.isEmpty())
                                showFolderContent((FolderItem)adapter.getItem(0));

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