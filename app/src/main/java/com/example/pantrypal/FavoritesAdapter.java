package com.example.pantrypal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<FavoriteRecipe> recipes;
    private Context context;
    private OnItemClickListener listener;

    public FavoritesAdapter(Context context, List<FavoriteRecipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteRecipe recipeData = recipes.get(position);
        String recipeName = recipeData.getName();
        String imageURL = recipeData.getimageURL();

        holder.recipeNameTextView.setText(recipeName);

        // Set the dimensions of the ImageView
        holder.recipeImageView.post(new Runnable() {
            @Override
            public void run() {
                int targetWidth = holder.recipeImageView.getWidth();
                int targetHeight = holder.recipeImageView.getHeight();

                if (!imageURL.contentEquals(" ")) {
                    Picasso.get().load(imageURL)
                            .placeholder(R.drawable.placeholder_image)
                            .resize(targetWidth, targetHeight) // Resize the image to match ImageView dimensions
                            .centerCrop() // Fit the image into ImageView with cropping
                            .into(holder.recipeImageView);
                } else {
                    holder.recipeImageView.setImageResource(R.drawable.placeholder_image);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView recipeNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });

        }
    }
}
