package com.timitoc.groupic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.FolderItem;
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.utils.VolleySingleton;

import java.util.ArrayList;

/**
 * Created by timi on 28.04.2016.
 */
public class MyImagesGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ImageItem> imageItems;
    private ImageLoader loader;

    public MyImagesGridAdapter(Context context, ArrayList<ImageItem> imageItems){
        this.context = context;
        this.imageItems = imageItems;
        this.loader = VolleySingleton.getInstance(null).getImageLoader();
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return imageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.images_grid_item, null);
        }
        NetworkImageView networkImageView = (NetworkImageView)convertView.findViewById(R.id.network_image_view);
        networkImageView.setDefaultImageResId(R.drawable.ic_launcher);
        networkImageView.setAdjustViewBounds(true);
        networkImageView.setImageUrl(imageItems.get(position).getRequestUrl(), loader);


        return convertView;
    }
}
