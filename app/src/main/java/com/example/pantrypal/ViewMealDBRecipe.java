package com.example.pantrypal;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;

import java.util.List;
import java.util.Map;

public class ViewMealDBRecipe extends AppCompatActivity {
    private final RecipeRetriever recipeRetriever = new RecipeRetriever("capstone-recipes-server-a64f8333ac1b.herokuapp.com");
    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        MealDBRecipe recipe = (MealDBRecipe) getIntent().getSerializableExtra("recipe");
        new Thread(() -> {
            Bitmap recipeImage = recipeRetriever.getRecipeImage(recipe.getId(), false);
            runOnUiThread(() -> recipeImageView.setImageBitmap(recipeImage));
        }).start();

        // Initialize UI components
        recipeImageView = findViewById(R.id.imageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);

        recipeNameTextView.setText(recipe.getName());
        ingredientsTextView.setText(formatIngredientsList(recipe.getIngredients()));
        instructionsTextView.setText(formatList(recipe.getInstructionLines()));
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
