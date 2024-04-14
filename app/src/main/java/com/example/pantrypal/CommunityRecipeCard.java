package com.example.pantrypal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pantrypal.activities.ViewRecipe;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class CommunityRecipeCard extends Activity {
    private final CardView cardView;
    private final ImageView imageView;
    private final TextView textView;
    private FirebaseFirestore db;

    private String recipeId;

    public CommunityRecipeCard(CardView cardView) {
        this(cardView,
                (ImageView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(0),
                (TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(1));
    }

    public CommunityRecipeCard(CardView cardView, ImageView recipeImageView, TextView recipeNameView) {
        this.cardView = cardView;
        this.imageView = recipeImageView;
        this.textView = recipeNameView;
    }

    public void setRecipe(String recipeId) {
        this.recipeId = recipeId;

        db = FirebaseFirestore.getInstance();

        db.collection("recipes").document(recipeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            // Extract recipe details from the document
                            String recipeName = task.getResult().getString("Name");
                            String imageUrl = task.getResult().getString("ImageUrl");
                            textView.setText(recipeName);
                            if (imageUrl != null) {
                                // Load image asynchronously
                                imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        int targetWidth = imageView.getWidth();
                                        int targetHeight = imageView.getHeight();

                                        Picasso.get().load(imageUrl)
                                                .placeholder(R.drawable.placeholder_image)
                                                .resize(targetWidth, targetHeight)
                                                .centerCrop()
                                                .into(imageView);
                                    }
                                });
                            } else {
                                // Set placeholder image
                                imageView.setImageResource(R.drawable.placeholder_image);
                            }
                        } else {
                            Log.d("ViewRecipe", "No such document");
                        }
                    } else {
                        Log.d("ViewRecipe", "get failed with ", task.getException());
                    }
                });
    }



    public void setOnClickListener(Context context) {
        cardView.setOnClickListener(listener -> {
            Intent intent = new Intent(context, ViewRecipe.class);
            intent.putExtra("recipeId", recipeId);
            context.startActivity(intent);
        });
    }
}