package com.example.pantrypal;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;
import java.util.Map;

public class ViewMealDBRecipe extends AppCompatActivity {
    private final RecipeRetriever recipeRetriever = new RecipeRetriever("capstone-recipes-server-a64f8333ac1b.herokuapp.com");
    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private Button backButton;
    private ToggleButton favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        MealDBRecipe recipe = (MealDBRecipe) getIntent().getSerializableExtra("recipe");
        new Thread(() -> {
            Bitmap recipeImage = recipeRetriever.getRecipeImage(recipe.getImageURL(), false);
            runOnUiThread(() -> recipeImageView.setImageBitmap(recipeImage));
        }).start();

        // Initialize UI components
        recipeImageView = findViewById(R.id.imageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.favoriteButton);

        
        recipeNameTextView.setText(recipe.getName());
        ingredientsTextView.setText(formatIngredientsList(recipe.getIngredients()));
        instructionsTextView.setText(formatList(recipe.getInstructionLines()));


        // Set onClickListener for backButton to finish current activity and go back to previous

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Close the current activity and return to the previous one
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recipeRetriever.shutdown();
    }

    private String formatList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append("- ").append(item).append("\n");
        }
        return stringBuilder.toString();
    }

    private String formatIngredientsList(List<Map.Entry<String, String>> ingredients) {
        StringBuilder ingredientQuantityList = new StringBuilder();
        for (Map.Entry<String, String> ingredientQuantityPair : ingredients) {
            ingredientQuantityList.append(
                    String.format("- %s (%s)\n", ingredientQuantityPair.getKey(), ingredientQuantityPair.getValue()));
        }

        return ingredientQuantityList.toString();
    }
}
