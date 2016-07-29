package com.timitoc.groupic.dialogBoxes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by timi on 01.05.2016.
 */
public class CreateFolderDialogBox extends DialogFragment {

    View mainView;

    private boolean invalid(String data)
    {
        if (data.length() == 0){
            Toast.makeText(this.getActivity(), "Name can not be empty", Toast.LENGTH_SHORT).show();
            return true;
        }
        for (int i = 0; i < data.length(); i++)
            if (!Character.isDigit(data.charAt(i)) && !Character.isLetter(data.charAt(i)) && !(data.charAt(i) == ' ')){
                String error = data.charAt(i) + " is not allowed when creating folders.";
                Toast.makeText(this.getActivity(), error, Toast.LENGTH_SHORT).show();
                return true;
            }
        return false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();


        builder.setView(mainView = inflater.inflate(R.layout.create_folder_dialog, null))
                // Add action buttons
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((TextView)mainView.findViewById(R.id.folder_create_name)).getText().toString();
                        if (!invalid(name)) {
                            try {
                                createFolder(name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                giveError();
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CreateFolderDialogBox.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void giveError() {
        System.out.println("Network error, are you connected to internet?");
    }

    public void createFolder(String name) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("title", name);
        params.put("group_id", Global.current_group_id);
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if ("success".equals(jsonResponse.getString("status"))) {
                                System.out.println("Success in creating folder");
                            }
                            else {
                                giveError();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            giveError();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                        giveError();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "create_folder");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        queue.add(strRequest);
    }

}
