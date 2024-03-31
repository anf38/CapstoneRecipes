package com.example.pantrypal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;

public class RecipeCard extends Activity {
    private final CardView cardView;
    private final ImageView imageView;
    private final TextView textView;
    private MealDBRecipe recipe = null;

    public RecipeCard(CardView cardView) {
        this(cardView,
                (ImageView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(0),
                (TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(1));
    }

    public RecipeCard(CardView cardView, ImageView recipeImageView, TextView recipeNameView) {
        this.cardView = cardView;
        this.imageView = recipeImageView;
        this.textView = recipeNameView;
    }

    public void setRecipe(MealDBRecipe recipe) {
        this.recipe = recipe;

        new Thread(() -> {
            RecipeRetriever recipeRetriever = new RecipeRetriever("capstone-recipes-server-a64f8333ac1b.herokuapp.com");
            Bitmap recipeImage = recipeRetriever.getRecipeImage(recipe.getImageURL(), false);
            new Handler(Looper.getMainLooper()).post(() -> {
                imageView.setImageBitmap(recipeImage);
                textView.setText(recipe.getName());
            });
            recipeRetriever.shutdown();
        }, "loadRecipeImage").start();
    }

    public void setOnClickListener(Context context) {
        cardView.setOnClickListener(listener -> {
            Intent intent = new Intent(context, ViewMealDBRecipe.class);
            intent.putExtra("recipe", recipe);
            context.startActivity(intent);
        });
    }
}
