package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    private List<String> recipes;
    private Context context;

    public CustomAdapter(Context context, int resource, List<String> recipes) {
        super(context, resource, recipes);
        this.recipes = recipes;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.favorite_list_item, parent, false);
        }

        ImageView recipeImageView = convertView.findViewById(R.id.recipeImageView);
        TextView recipeNameTextView = convertView.findViewById(R.id.recipeNameTextView);

        String[] recipeData = recipes.get(position).split("\\|");
        String recipeName = recipeData[0];
        String imageURL = recipeData.length > 1 ? recipeData[1] : "";

        // Set recipe name
        recipeNameTextView.setText(recipeName);

        // Load image using Picasso
        if (!imageURL.isEmpty()) {
            Picasso.get().load(imageURL).placeholder(R.drawable.placeholder_image).into(recipeImageView);
        } else {
            recipeImageView.setImageResource(R.drawable.placeholder_image); // Set default image if URL is empty
        }

        return convertView;
    }
}