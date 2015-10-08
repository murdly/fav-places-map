package com.example.arkadiuszkarbowy.maps.route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.arkadiuszkarbowy.maps.R;
import com.example.arkadiuszkarbowy.maps.db.MyPlace;

import java.util.List;

/**
 * Created by arkadiuszkarbowy on 07/10/15.
 */
public class AvailablePlacesAdapter extends ArrayAdapter<MyPlace> {
    public AvailablePlacesAdapter(Context context, List<MyPlace> places) {
        super(context, R.layout.available_item, places);
    }

    public static class ViewHolder {
        public TextView mName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.available_item, parent, false);
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyPlace item = getItem(position);
        holder.mName.setText(item.getTitle());
        return convertView;
    }
}