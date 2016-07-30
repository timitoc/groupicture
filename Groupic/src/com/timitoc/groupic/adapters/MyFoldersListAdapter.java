package com.timitoc.groupic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.FolderItem;

import java.util.ArrayList;

/**
 * Created by timi on 28.04.2016.
 */
public class MyFoldersListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FolderItem> groupItems;

    public MyFoldersListAdapter(Context context, ArrayList<FolderItem> groupItems){
        this.context = context;
        this.groupItems = groupItems;
    }

    @Override
    public int getCount() {
        return groupItems.size();
    }

    @Override
    public Object getItem(int position) {
        return groupItems.get(position);
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
            convertView = mInflater.inflate(R.layout.folder_list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.folder_title);

        txtTitle.setText(groupItems.get(position).getTitle());
        txtTitle.setBackgroundColor(groupItems.get(position).getColor());

        return convertView;
    }

}