package com.example.arkadiuszkarbowy.maps.search;

/**
 * Created by arkadiuszkarbowy on 01/10/15.
 */

import com.example.arkadiuszkarbowy.maps.R;
import com.google.android.gms.location.places.AutocompletePrediction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.StringTokenizer;

public class AutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> {
    public AutocompleteAdapter(Context context, List<AutocompletePrediction> resultsList) {
        super(context, R.layout.search_item, resultsList);
    }

    public static class ViewHolder {
        public TextView mName;
        public TextView mCity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_item, parent, false);
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mCity = (TextView) convertView.findViewById(R.id.city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AutocompletePrediction item = getItem(position);
        holder.mName.setText(getPrimaryText(item));
        holder.mCity.setText(getSecondaryText(item));
        return convertView;
    }

    private String getPrimaryText(AutocompletePrediction item) {
        StringTokenizer st = new StringTokenizer(item.getDescription(), ",");
        String primary = "";
        if (st.hasMoreTokens())
            primary = st.nextToken();
        return primary;
    }

    private String getSecondaryText(AutocompletePrediction item) {
        String s = item.getDescription();
        int start = s.indexOf(",");
        return start != -1 ? s.substring(++start).trim() : getPrimaryText(item);
    }
}