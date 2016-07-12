package com.timitoc.groupic.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.timitoc.groupic.R;
import com.timitoc.groupic.models.GroupItem;
import com.timitoc.groupic.utils.interfaces.GroupEnterCallback;
import com.timitoc.groupic.utils.ViewUtils;

import java.util.ArrayList;

public class MyGroupsListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GroupItem> groupItems;
    private boolean allowViewExpansion;
    private GroupEnterCallback expansionButtonAction;

    public MyGroupsListAdapter(Context context, ArrayList<GroupItem> groupItems){
        this.context = context;
        this.groupItems = groupItems;
    }

    public MyGroupsListAdapter(Context context, ArrayList<GroupItem> groupItems, GroupEnterCallback expansionButtonAction) {
        this(context, groupItems);
        this.expansionButtonAction = expansionButtonAction;
        allowViewExpansion = true;
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

        if (allowViewExpansion && expansionButtonAction != null)
            buildViewExpansion(position, convertView, parent);

        return convertView;
    }

    private void buildViewExpansion(int position, View view, ViewGroup parent) {
        final GroupItem selectedItem = (GroupItem) getItem(position);
        final View summary;
        Button enterButton;
        EditText inputText = null;
        final TextView feedbackInfo;
        if (!selectedItem.hasPassword()) {
            summary = view.findViewById(R.id.no_pass_layout);
            enterButton = (Button) summary.findViewById(R.id.no_pass_enter);
            feedbackInfo = (TextView) summary.findViewById(R.id.info_no_pass);
        }
        else {
            summary = view.findViewById(R.id.has_pass_layout);
            enterButton = (Button) summary.findViewById(R.id.has_pass_enter);
            inputText = (EditText) summary.findViewById(R.id.has_pass_input);
            feedbackInfo = (TextView) summary.findViewById(R.id.info_has_pass);
        }
        final EditText finalInputText = inputText;
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPassword = null;
                if (finalInputText != null)
                    inputPassword = finalInputText.getText().toString();
                expansionButtonAction.call(selectedItem, inputPassword, feedbackInfo);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Calling toggle");
                ViewUtils.toggle(summary);
            }
        });
    }

    public void setAllowViewExpansion(boolean h) {
        allowViewExpansion = h;
    }

    public boolean doesAllowViewExpansion() {
        return allowViewExpansion;
    }

}