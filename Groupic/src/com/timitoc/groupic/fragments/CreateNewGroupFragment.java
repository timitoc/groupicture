package com.timitoc.groupic.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.adapters.MyGroupsListAdapter;
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timi on 01.05.2016.
 */
public class CreateNewGroupFragment extends Fragment {

    View mainView;
    Button createButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.create_new_group_fragment, container, false);
        createButton = (Button) mainView.findViewById(R.id.new_group_button);
        prepare();
        return  mainView;
    }

    void prepare() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Started creating group");
                String name = ((EditText) mainView.findViewById(R.id.new_group_name)).getText().toString();
                String description = ((EditText) mainView.findViewById(R.id.new_group_description)).getText().toString();
                try {
                    createNewGroup(name, description);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createNewGroup(String name, String description) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("name", name);
        params.put("description", description);
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
                                Toast.makeText(getActivity(), "Group created successfully", Toast.LENGTH_SHORT).show();
                                int group_id = jsonResponse.getInt("id");
                                System.out.println("Obtained id is: " + group_id);
                                mapGroupToUser(group_id, getActivity());
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
                paramap.put("function", "create_new_group");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        queue.add(strRequest);
    }

    public static void mapGroupToUser(int group_id, final Activity activity) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = activity.getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        System.out.println("Want to map " + Global.user_id + " with " + group_id + " ");
        params.put("user_id", Global.user_id);
        params.put("group_id", group_id);
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
                                System.out.println("e bine");
                            }
                            else
                                Toast.makeText(activity, "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                        Toast.makeText(activity, "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "map_group_to_user");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        queue.add(strRequest);
    }

}