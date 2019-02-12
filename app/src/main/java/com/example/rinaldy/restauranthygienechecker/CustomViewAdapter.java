package com.example.rinaldy.restauranthygienechecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomViewAdapter extends ArrayAdapter<Establishment> implements Filterable {

    private ArrayList<Establishment> mEstablishments;

    // view holder that acts as a cache
    public static class ViewHolder {
        public MarqueeTextView name;
        public MarqueeTextView address;
        public TextView distance;
        public ImageView image;
    }

    public CustomViewAdapter(Context context, ArrayList<Establishment> establishments) {
        super(context, R.layout.list_view_row, establishments);
        this.mEstablishments = establishments;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Establishment e = (Establishment) getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) { // a new view, add to cache
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_row, parent, false);
            viewHolder.name = (MarqueeTextView) convertView.findViewById(R.id.list_row_name);
            viewHolder.address = (MarqueeTextView) convertView.findViewById(R.id.list_row_address);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.list_row_distance);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.list_row_image);
            convertView.setTag(viewHolder);
        } else { // recycle previous view from cache
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(e.getBusinessName());
        viewHolder.address.setText(e.getFullAddress(", "));
        viewHolder.distance.setText(Utils.getDistanceText(e));
        viewHolder.image.setBackgroundResource(Utils.getRatingIcon(e));

        return convertView;
    }

}
