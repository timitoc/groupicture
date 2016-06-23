package com.timitoc.groupic.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timitoc.groupic.R;
import com.timitoc.groupic.activities.BigImageViewActivity;
import com.timitoc.groupic.adapters.MyImagesGridAdapter;
import com.timitoc.groupic.dialogBoxes.AddNewDialogBox;
import com.timitoc.groupic.dialogBoxes.SaveImageOnLocalDialogBox;
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.utils.Encryptor;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.SaveLocalManager;
import com.timitoc.groupic.utils.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by timi on 28.04.2016.
 */
public class FolderContentFragment extends Fragment {

    MyImagesGridAdapter adapter;
    View mainView;
    GridView gridView;
    private int folderId;
    private Bitmap bitmap;

    public static FolderContentFragment newInstance(int folderId) {
        FolderContentFragment myFragment = new FolderContentFragment();

        Bundle args = new Bundle();
        args.putInt("folder-id", folderId);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.folder_content_fragment, container, false);
        gridView = (GridView)mainView.findViewById(R.id.images_grid_view);
        folderId = getArguments().getInt("folder-id");
        VolleySingleton.getInstance(getActivity());
        Global.onAddMenuItemClicked = new Runnable() {
            @Override
            public void run() {
                Global.current_folder_id = folderId;
                Global.addImage = new Runnable() {
                    @Override
                    public void run() {
                        addImage();
                    }
                };
                new AddNewDialogBox().show(getFragmentManager(), "1");
            }
        };
        prepare();
        return  mainView;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Global.PICK_IMAGE_REQUEST);
    }

    private void addImage() {
        showFileChooser();
    }

    private void uploadImage() throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("function", "upload_image_in_folder");
        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        final JSONObject params = new JSONObject();

        params.put("id", folderId); /// id-ul folderului curent
        params.put("image", Global.getStringImage(bitmap));
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        System.out.println("Hash length is: " + hash.length());
        System.out.println("Params length is: " + params.toString().length());
        builder.appendQueryParameter("data", params.toString());
        builder.appendQueryParameter("hash", hash);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "upload_image_in_folder");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        System.out.println(strRequest.getUrl());
        queue.add(strRequest);
    }

    private void prepare() {
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        System.out.println("This folder id is " + folderId);

        try {
            populateImageItems(imageItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
/*
        folderItems.add(new FolderItem(1, "titlu"));
        folderItems.add(new FolderItem(2, "yey"));*/
        adapter = new MyImagesGridAdapter(getActivity(), imageItems);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageItem item = (ImageItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), BigImageViewActivity.class);
                intent.putExtra("request_url", item.getRequestUrl());
                intent.putExtra("index", i);
                intent.putExtra("request_array", adapter.getRequestArray());
                //Start details activity
                startActivity(intent);
                System.out.println(item.getId() + " image clicked ");
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageItem item = (ImageItem) adapterView.getItemAtPosition(i);
                processSaveOnLocalRequest(item);
                return true;
            }
        });
    }

    private void processSaveOnLocalRequest(ImageItem item) {
        if (SaveLocalManager.alreadySaved(item))
            askToDeleteImageOnLocal(item);
        else
            askToSaveImageOnLocal(item);
    }

    private void askToDeleteImageOnLocal(ImageItem item) {
        System.out.println("Wish to delete this");
    }

    private void askToSaveImageOnLocal(ImageItem item) {
        SaveLocalManager.prepare(item);
        if (Global.confirm_save_image_on_local) {
            new SaveImageOnLocalDialogBox().show(getFragmentManager(), "2");
        }
        else
            SaveLocalManager.savePrepared();
    }

    private String buildImageRequestUrl(int image_id) throws JSONException {
        String url = getString(R.string.image_service_url);
        Uri.Builder builder = Uri.parse(url).buildUpon();

        builder.appendQueryParameter("public_key", Global.MY_PUBLIC_KEY);
        final JSONObject params = new JSONObject();
        params.put("id", image_id);
        final String hash = Encryptor.hash(params.toString() + Global.MY_PRIVATE_KEY);
        builder.appendQueryParameter("data", params.toString());
        builder.appendQueryParameter("hash", hash);
        return  builder.build().toString();
    }

    void populateImageItems(final ArrayList<ImageItem> imageItems) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = getString(R.string.api_service_url);
        final JSONObject params = new JSONObject();
        params.put("id", folderId); /// id-ul folderului curent
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
                                JSONArray imagesId = jsonResponse.getJSONArray("images");
                                for (int i = 0; i < imagesId.length(); i++) {
                                    int image_id = imagesId.getInt(i);
                                    ImageItem item = new ImageItem(image_id, "not_yet_implemented", buildImageRequestUrl(image_id));
                                    imageItems.add(item);
                                    System.out.println(item.getId() + " " + item.getRequestUrl());
                                }
                                gridView.setAdapter(adapter);
                            }
                            else
                                System.out.println("failed to get Images from folders");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("exception to get Images from folders");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Error " + error.getMessage());
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> paramap = new HashMap<>();
                paramap.put("function", "get_images_from_folder");
                paramap.put("public_key", Global.MY_PUBLIC_KEY);
                paramap.put("data", params.toString());
                paramap.put("hash", hash);
                return paramap;
            }
        };

        queue.add(strRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Global.PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                System.out.println(filePath.getEncodedPath());
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                System.out.println("Started uploading");
                uploadImage();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
