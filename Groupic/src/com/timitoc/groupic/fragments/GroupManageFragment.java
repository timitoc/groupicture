package com.timitoc.groupic.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyFoldersListAdapter;
import com.timitoc.groupic.dialogBoxes.AddNewDialogBox;
import com.timitoc.groupic.models.FolderItem;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.SaveLocalManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by timi on 28.04.2016.
 * Fragment regarding Group view within folders
 */
public class GroupManageFragment extends Fragment {

    View mainView;
    GroupItem groupItem;
    MyFoldersListAdapter adapter;
    ListView folderItemListView;
    private int prevClicked;

    int listedFolderPosition;

    public static GroupManageFragment newInstance(GroupItem groupItem) {
        GroupManageFragment myFragment = new GroupManageFragment();

        Bundle args = new Bundle();
        args.putSerializable("group_item", groupItem);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prevClicked = -1;
        mainView = inflater.inflate(R.layout.group_manage_fragment, container, false);
        groupItem =  (GroupItem) getArguments().getSerializable("group_item");
        Global.current_group_id = groupItem.getId();
        if (savedInstanceState == null)
            listedFolderPosition = 0;
        else {
            listedFolderPosition = savedInstanceState.getInt("listed");
        }
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
        adapter = new MyFoldersListAdapter(getActivity(),
                folderItems);
        folderItemListView = (ListView) mainView.findViewById(R.id.list_group_folders);

        folderItemListView.setAdapter(adapter);

        folderItemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FolderItem item = (FolderItem) adapterView.getItemAtPosition(i);
                if (prevClicked != -1){
                    folderItemListView.getChildAt(prevClicked).setBackgroundColor(getResources().getColor(R.color.acad1));
                }
                folderItemListView.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.acad3));
                prevClicked = i;
                listedFolderPosition = i;
                showFolderContent(item);

            }
        });
        folderItemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                FolderItem item = (FolderItem) adapterView.getItemAtPosition(i);
                int toastX, toastY;
                toastX = (int) (view.getX() + view.getWidth());
                toastY = (int) (view.getY() + view.getHeight());
                showFolderTitle(item, toastX, toastY);
                return true;
            }
        });

        try {
            searchServerForGroupFolders(folderItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showFolderTitle(FolderItem item, int toastX, int toastY){
        Toast toast= Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.LEFT, toastX, toastY);
        toast.show();
    }

    public void showFolderContent(FolderItem item) {
        System.out.println(item.getTitle() + " folder pressed ");
        Fragment fragment = FolderContentFragment.newInstance(item);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.folder_content, fragment)
                .commit();
    }

    public void searchServerForGroupFolders(final ArrayList<FolderItem> folderItems) throws JSONException {
        if (ConnectionStateManager.getUsingState() == ConnectionStateManager.UsingState.OFFLINE) {
            getFoldersFromLocal(folderItems);
            return;
        }
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

                                    folderItems.add(new FolderItem(arr.getJSONObject(i), groupItem));
                                }
                                adapter.notifyDataSetChanged();
                                ConnectionStateManager.increaseUsingState();
                                if (!adapter.isEmpty()) {
                                    folderItemListView.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            folderItemListView.getChildAt(listedFolderPosition).setBackgroundColor(getResources().getColor(R.color.acad3));
                                            prevClicked = listedFolderPosition;
                                        }
                                    });
                                    showFolderContent((FolderItem) adapter.getItem(listedFolderPosition));
                                }
                            }
                            else {
                                getFoldersFromLocal(folderItems);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            getFoldersFromLocal(folderItems);
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(("That didn't work!\n") + error.getMessage());
                getFoldersFromLocal(folderItems);
            }
        });
        System.out.println(jsonRequest.getUrl());
        queue.add(jsonRequest);
    }

    private void getFoldersFromLocal(ArrayList<FolderItem> folderItems) {
        ConnectionStateManager.decreaseUsingState();

        Set<String> folderSet = SaveLocalManager.getFoldersSet();
        for (String folderString : folderSet) {
            String[] fields = folderString.split("#");
            int groupId = Integer.parseInt(fields[1], 16);
            int folderId = Integer.parseInt(fields[2], 16);
            String title = fields[3];
            if (groupId == groupItem.getId()) {
                FolderItem folderItem= new FolderItem(folderId, title, groupItem, getResources().getColor(R.color.transparent));
                folderItems.add(folderItem);
            }
        }
        folderItemListView.setAdapter(adapter);
        if (!adapter.isEmpty()) {
            showFolderContent((FolderItem)adapter.getItem(0));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("listed", listedFolderPosition);
        super.onSaveInstanceState(savedInstanceState);
    }

}