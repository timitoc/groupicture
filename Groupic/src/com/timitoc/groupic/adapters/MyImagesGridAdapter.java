package com.timitoc.groupic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.volley.toolbox.ImageLoader;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.ImageItem;
import com.timitoc.groupic.utils.CustomNetworkImageView;
import com.timitoc.groupic.utils.SaveLocalManager;
import com.timitoc.groupic.utils.ViewHolder;
import com.timitoc.groupic.utils.VolleySingleton;

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
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.images_grid_item, null);
            holder = new ViewHolder((CustomNetworkImageView) convertView.findViewById(R.id.network_image_view));
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageItem imageItem = imageItems.get(position);
        if (SaveLocalManager.alreadySaved(imageItem)) {
            CustomNetworkImageView networkImageView = (CustomNetworkImageView) convertView.findViewById(R.id.network_image_view);
            networkImageView.setLocalImageBitmap(SaveLocalManager.getBitmapFromLocal(imageItem));

        }
        else {
            holder.image.setDefaultImageResId(R.drawable.ic_launcher);
            holder.image.setImageUrl(imageItems.get(position).getRequestUrl(), loader);
        }

        return convertView;
    }
}
