package com.example.pantrypal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
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
    private final RecipeRetriever recipeRetriever = new RecipeRetriever("capstone-recipes-server-a64f8333ac1b.herokuapp.com");

    public RecipeCard(CardView cardView) {
        this.cardView = cardView;
        imageView = (ImageView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(0);
        textView = (TextView) ((LinearLayout) cardView.getChildAt(0)).getChildAt(1);
    }

    public void setRecipe(MealDBRecipe recipe) {
        this.recipe = recipe;
        loadRecipe();
    }

    public void loadRecipe() {
        if (recipe == null)
            return;
        new Thread(() -> {
            Bitmap recipeImage = recipeRetriever.getRecipeImage(recipe.getId(), false);
            runOnUiThread(() -> {
                imageView.setImageBitmap(recipeImage);
                textView.setText(recipe.getName());
            });
        }, "loadRecipeImage").start();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        cardView.setOnClickListener(onClickListener);
    }
}
