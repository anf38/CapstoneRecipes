package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

public class SearchListAdapter extends ArrayAdapter<ResultsRecipe> implements Filterable {
    private static final String TAG = "SearchListAdapter";
    private Context mContext;
    private int mResource;

    public SearchListAdapter(Context context, int resource, ArrayList<ResultsRecipe> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the current recipe object
        ResultsRecipe currentRecipe = getItem(position);

        // Check if the view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        // Get references to the TextViews and ImageView in the list item layout
        TextView titleTV = convertView.findViewById(R.id.textViewRecipeName);
        TextView idTV = convertView.findViewById(R.id.hiddenId);
        ImageView imageView = convertView.findViewById(R.id.textViewUri);

        // Set the text and ID to the TextViews
        titleTV.setText(currentRecipe.getTitle());
        idTV.setText(currentRecipe.getId());

        // Load and display the recipe image using Picasso
        Picasso.get().load(currentRecipe.getImageUrl()).into(imageView);

        return convertView;
    }
}
