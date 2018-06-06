package com.google.scheduler.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.scheduler.R;

import java.util.ArrayList;

/**
 * Created by kfugaban on 6/6/18.
 */

public class MainListAdapter extends ArrayAdapter<DataModel> {
    private ArrayList<DataModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txt_name;
        TextView txt_tierGroup;
        TextView txt_role;
        TextView txt_time;
    }

    public MainListAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.custom_list_row, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_list_row, parent, false);
            viewHolder.txt_name = convertView.findViewById(R.id.txt_name);
            viewHolder.txt_tierGroup = convertView.findViewById(R.id.txt_tierGroup);
            viewHolder.txt_role = convertView.findViewById(R.id.txt_role);
            viewHolder.txt_time = convertView.findViewById(R.id.txt_time);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.txt_name.setText(dataModel.getName());
        viewHolder.txt_tierGroup.setText(dataModel.getTierGroup());
        viewHolder.txt_role.setText(dataModel.getRole());
        viewHolder.txt_time.setText(dataModel.getTime());
        return convertView;
    }
}
