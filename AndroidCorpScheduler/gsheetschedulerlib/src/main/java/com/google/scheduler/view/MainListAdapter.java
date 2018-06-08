package com.google.scheduler.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.scheduler.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import static com.google.scheduler.constants.AppConstants.PH_TIMEZONE;

/**
 * Created by kfugaban on 6/6/18.
 */

public class MainListAdapter extends ArrayAdapter<DataModel> {
    private List<DataModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txt_name;
        TextView txt_role;
        TextView txt_time_start;
        TextView txt_time_end;
    }

    public MainListAdapter(List<DataModel> data, Context context) {
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

            if(!dataSet.isEmpty()) {
                convertView = inflater.inflate(R.layout.custom_list_row, parent, false);
                viewHolder.txt_name = convertView.findViewById(R.id.txt_name);
                viewHolder.txt_role = convertView.findViewById(R.id.txt_role);
                viewHolder.txt_time_start = convertView.findViewById(R.id.txt_time_start);
                viewHolder.txt_time_end = convertView.findViewById(R.id.txt_time_end);
            }else{
                convertView = inflater.inflate(R.layout.empty_list_row, parent, false);
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(!dataSet.isEmpty()) {
            viewHolder.txt_name.setText(dataModel.getName());
            viewHolder.txt_role.setText(dataModel.getRole());

            if(dataModel.getTime() != null) {

                    DateFormat sdf3 = new SimpleDateFormat("MMMM/dd \n hh:mm a");
                    sdf3.setTimeZone(TimeZone.getTimeZone(PH_TIMEZONE));

                    String startTimeFormatted = sdf3.format(dataModel.getTime().toDate());
                    String endTimeFormatted = sdf3.format(dataModel.getTime().plusHours(9).toDate());
                    viewHolder.txt_time_start.setText(startTimeFormatted);
                    viewHolder.txt_time_end.setText(endTimeFormatted);
            }

        }
        return convertView;
    }
}
