package com.timitoc.groupic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.models.NavDrawerItem;
import com.timitoc.groupic.utils.Global;
import com.timitoc.groupic.utils.ViewUtils;

import java.util.ArrayList;

public class MyGroupsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GroupItem> groupItems;

    public MyGroupsListAdapter(Context context, ArrayList<GroupItem> groupItems){
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
            convertView = mInflater.inflate(R.layout.group_list_item, null);
        }

        TextView txtTitle = (TextView) convertView.findViewById(R.id.group_title);
        TextView txtDescription = (TextView) convertView.findViewById(R.id.group_description);

        txtTitle.setText(groupItems.get(position).getTitle());
        txtDescription.setText(groupItems.get(position).getDescription());
        View summary = convertView.findViewById(R.id.bonus_text);
        ViewUtils.addAction(convertView, summary, Global.baseActivity);

        return convertView;
    }

}