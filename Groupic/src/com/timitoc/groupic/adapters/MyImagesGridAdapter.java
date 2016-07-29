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
import com.timitoc.groupic.utils.*;

import java.util.ArrayList;

/**
 * Created by timi on 28.04.2016.
 */
public class MyImagesGridAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ImageItem> imageItems;
    private ImageLoader loader;
    private String[] requestArray;

    public MyImagesGridAdapter(Context context, ArrayList<ImageItem> imageItems){
        this.context = context;
        this.imageItems = imageItems;
        this.loader = VolleySingleton.getInstance(null).getImageLoader();
    }

    public String[] getRequestArray()
    {
        requestArray = new String[imageItems.size()];
        for (int i = 0; i < imageItems.size(); i++)
            requestArray[i] = imageItems.get(i).getRequestUrl();
        System.out.println("request array length on get " + requestArray.length);
        return requestArray;
    }

    public ArrayList<ImageItem> getImageItems() {
        return imageItems;
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

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.images_grid_item, null);
            holder = new ViewHolder((CustomNetworkImageView) convertView.findViewById(R.id.network_image_view));
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            //holder.image.setImageUrl(null, loader);
            //holder.image.setImageBitmap(null);
        }

        ImageItem imageItem = imageItems.get(position);
        if (SaveLocalManager.alreadySaved(imageItem)) {
            System.out.println("Getting item " + imageItem.getId() + " from local ");
            CustomNetworkImageView networkImageView = (CustomNetworkImageView) convertView.findViewById(R.id.network_image_view);
            int oldWidth = networkImageView.getLayoutParams().width;
            int oldHeight = networkImageView.getLayoutParams().height;
            //networkImageView.setLocalImageBitmap(Global.getScaledBitmap(oldWidth, oldHeight, SaveLocalManager.getBitmapFromLocal(imageItem)));
            networkImageView.setLocalImageBitmap(SaveLocalManager.getBitmapFromLocal(imageItem));

        }
        else {
            holder.image.setDefaultImageResId(R.drawable.ic_launcher);
            //holder.image.setAdjustViewBounds(true);
            //holder.image.setImageBitmap(null);
            //holder.image.setImageUrl(null, loader);
            holder.image.setImageUrl(imageItems.get(position).getRequestUrl(), loader);
            System.out.println("Adapter request to get view for image on position " + position + " with id " + imageItems.get(position).getId() + " with url " + imageItems.get(position).getRequestUrl());
        }

        return convertView;
    }
}
