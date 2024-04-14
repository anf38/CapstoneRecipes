package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SearchListAdapter extends ArrayAdapter<ResultsRecipe> implements Filterable {
    private static final String TAG = "SearchListAdapter";
    private Context mContext;
    private int mResource;

    public SearchListAdapter(Context context, int resource, ArrayList<ResultsRecipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String title = getItem(position).getTitle();
        String id = getItem(position).getId();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView titleTV = (TextView) convertView.findViewById(R.id.textViewRecipeName);
        TextView idTV = (TextView) convertView.findViewById(R.id.hiddenId);

        titleTV.setText(title);
        idTV.setText(id);

        return convertView;
    }
}
