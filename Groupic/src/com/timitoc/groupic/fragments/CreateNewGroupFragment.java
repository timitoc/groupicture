package com.timitoc.groupic.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.CreateNewGroupFragmentModel;
import com.timitoc.groupic.utils.ConnectionStateManager;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.interfaces.ServerStatusCallback;
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
    CreateNewGroupFragmentModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("Creation");
        mainView = inflater.inflate(R.layout.create_new_group_fragment, container, false);
        createButton = (Button) mainView.findViewById(R.id.new_group_button);
        if (getArguments() != null && getArguments().containsKey("create-model"))
            model = (CreateNewGroupFragmentModel) getArguments().getSerializable("create-model");
        populate();
        prepare();
        return  mainView;
    }

    @Override
    public void onPause() {
        String name = ((EditText) mainView.findViewById(R.id.new_group_name)).getText().toString();
        String description = ((EditText) mainView.findViewById(R.id.new_group_description)).getText().toString();
        String password = ((EditText) mainView.findViewById(R.id.new_group_password)).getText().toString();
        model.setName(name);
        model.setDescription(description);
        model.setPassword(password);
        super.onPause();
    }

    private void populate() {
        ((EditText) mainView.findViewById(R.id.new_group_name)).setText(model.getName());
        ((EditText) mainView.findViewById(R.id.new_group_description)).setText(model.getDescription());
        ((EditText) mainView.findViewById(R.id.new_group_password)).setText(model.getPassword());
    }

    void prepare() {
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConnectionStateManager.getUsingState() != ConnectionStateManager.UsingState.ONLINE) {
                    Toast.makeText(getActivity(), "Can't create groups while offline", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("Started creating group");
                if (!checkInput())
                    return;
                try {
                    String name = ((EditText) mainView.findViewById(R.id.new_group_name)).getText().toString();
                    String description = ((EditText) mainView.findViewById(R.id.new_group_description)).getText().toString();
                    String password = ((EditText) mainView.findViewById(R.id.new_group_password)).getText().toString();
                    createNewGroup(name, description, password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String[] aux = {"name", "description", "password"};
    private boolean invalid(String data, int str)
    {
        for (int i = 0; i < data.length(); i++)
            if (!Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i)) && !(data.charAt(i) == ' ')){
                String error = data.charAt(i) + " is not allowed in " + aux[str] + ".";
                Toast.makeText(this.getActivity(), error, Toast.LENGTH_SHORT).show();
                return true;
            }
        return false;
    }

    /**
     * Shows the errors within the input data.
     * @return Returns true if input is sanitized.
     */
    private boolean checkInput() {
        // Better feedback coming soon

        String name = ((EditText) mainView.findViewById(R.id.new_group_name)).getText().toString();
        String description = ((EditText) mainView.findViewById(R.id.new_group_description)).getText().toString();
        String password = ((EditText) mainView.findViewById(R.id.new_group_password)).getText().toString();

        /*System.out.println("the password is " + password);
        System.out.println("the password hash is " + Encryptor.hash(password));
        return false;*/

        if (name.isEmpty()) {
            Toast.makeText(this.getActivity(), "Empty name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return !(invalid(name, 0) || invalid(description, 1) || (!password.isEmpty() && invalid(password, 2)));
    }

    private void createNewGroup(String name, String description, final String password) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("name", name);
        params.put("description", description);
        params.put("password", password.isEmpty() ? "" : Encryptor.hash(password));
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
                                ((EditText) mainView.findViewById(R.id.new_group_name)).getText().clear();
                                ((EditText) mainView.findViewById(R.id.new_group_description)).getText().clear();
                                ((EditText) mainView.findViewById(R.id.new_group_password)).getText().clear();
                                int group_id = jsonResponse.getInt("id");
                                System.out.println("Obtained id is: " + group_id);
                                mapGroupToUser(group_id, getActivity(), password);
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

    public static void mapGroupToUser(int group_id, final Activity activity, String input) throws JSONException {
        mapGroupToUser(group_id, activity, input, null);
    }

    public static void mapGroupToUser(int group_id, final Activity activity, String input, @Nullable final ServerStatusCallback callback) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = activity.getString(R.string.api_service_url);
        String password = (input==null || input.isEmpty()) ? "" : Encryptor.hash(input);
        System.out.println(password);
        final JSONObject params = new JSONObject();
        System.out.println("Want to map " + Global.user_id + " with " + group_id + " ");
        params.put("user_id", Global.user_id);
        params.put("group_id", group_id);
        params.put("password", password);
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (callback != null)
                                callback.onStatus(jsonResponse.getString("status"), jsonResponse.getString("detail"));
                            if ("success".equals(jsonResponse.getString("status")))
                                System.out.println("e bine");
                            else {
                                //Toast.makeText(activity, "Network error, are you connected to the internet?", Toast.LENGTH_SHORT).show();
                                System.out.println(jsonResponse.getString("status") + " " + jsonResponse.getString("detail"));
                            }
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
