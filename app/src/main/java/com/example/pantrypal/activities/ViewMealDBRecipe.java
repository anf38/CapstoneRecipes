package com.example.pantrypal.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewMealDBRecipe extends AppCompatActivity {
    private RecipeRetriever recipeRetriever;
    private ImageView recipeImageView;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Button backButton;
    private MealDBRecipe recipe;
    private ToggleButton favoriteButton;
    private ArrayList<String> tags = new ArrayList<>();
    private TextView tagsBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        recipeRetriever = RecipeRetriever.getInstance();

        recipe = (MealDBRecipe) getIntent().getSerializableExtra("recipe");
        new Thread(() -> {
            Bitmap recipeImage = recipeRetriever.getRecipeImage(recipe.getImageUrl(), false);
            runOnUiThread(() -> recipeImageView.setImageBitmap(recipeImage));
        }).start();

        // Initialize UI components
        recipeImageView = findViewById(R.id.imageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        tagsBox = findViewById(R.id.tagsBox);


        recipeNameTextView.setText(recipe.getTitle());
        ingredientsTextView.setText(formatIngredientsList(recipe.getIngredients()));
        instructionsTextView.setText(formatList(recipe.getInstructionLines()));

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteStatus(recipe);
            }
        });

        // Set onClickListener for backButton to finish current activity and go back to previous

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to the previous one
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck the favorite status and update the UI
        if (recipe != null) {
            checkIfRecipeIsFavorite(recipe);
        }
    }

    private void checkIfRecipeIsFavorite(MealDBRecipe recipe) {
        if (currentUser != null) {
            String recipeID = String.valueOf(recipe.getId());
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeID);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isFavorite = task.getResult().exists();
                    // Update UI based on whether recipe is favorite or not
                    updateFavoriteButtonUI(isFavorite);
                } else {
                    // Handle errors
                    Toast.makeText(ViewMealDBRecipe.this, "Error checking favorite status: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String formatList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append("- ").append(item).append("\n");
            if (item.contains("pecan") ||
                    item.contains("cashew") ||
                    item.contains("walnut") ||
                    item.contains("almond") ||
                    item.contains("pistachio") ||
                    item.contains("macadamia") ||
                    item.contains("peanut")) {
                tags.add("tree nuts");
            }

            if (item.contains("milk") ||
                    item.contains("butter") ||
                    item.contains("cheese")) {
                tags.add("Dairy");
            }

            if (item.contains("bread") || item.contains("flour")) {
                tags.add("Gluten");
            }

            if (item.contains("egg")) {
                tags.add("Eggs");
            }

            if (item.contains("chicken")) {
                tags.add("Chicken");
            }

            if (item.contains("beef") || item.contains("steak")) {
                tags.add("Beef");
            }

            if (item.contains("pork")) {
                tags.add("Pork");
            }

            if (item.contains("fish") ||
                    item.contains("salmon")
                    || item.contains("anchov")
                    || item.contains("tuna")) {
                tags.add("Fish");
            }

            if (item.contains("mussel") ||
                    item.contains("shrimp") ||
                    item.contains("clam") ||
                    item.contains("crab") ||
                    item.contains("lobster") ||
                    item.contains("scallop") ||
                    item.contains("crawfish")) {
                tags.add("shellfish");
            }

            if (item.contains("sesame")) {
                tags.add("Sesame");
            }
        }
        StringBuilder tagsText = new StringBuilder();
        tagsText.append("Tags: ");
        Set<String> uniqueTags = new HashSet<>();

        for (String tag : tags) {
            if (!uniqueTags.contains(tag)) {
                tagsText.append(tag).append(", ");
                uniqueTags.add(tag);
            }
        }


        // Remove the last comma and space
        if (tagsText.length() > 0) {
            tagsText.setLength(tagsText.length() - 2);
        }

        tagsBox.setText(tagsText.toString());

        return stringBuilder.toString();
    }

    private String formatIngredientsList(List<String> ingredients) {
        StringBuilder ingredientQuantityList = new StringBuilder();
        for (String ingredient : ingredients) {
            ingredientQuantityList.append("- ").append(ingredient).append("\n");
        }

        return ingredientQuantityList.toString();
    }

    private void toggleFavoriteStatus(MealDBRecipe recipe) {
        int recipeId = recipe.getIDInt();

        // Convert the recipeId to a string
        String recipeIdString = String.valueOf(recipeId);

        // Check if the recipe is already in favorites
        if (currentUser != null) {
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeIdString);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isFavorite = task.getResult().exists();
                    if (isFavorite) {
                        // If already in favorites, remove it

                        removeRecipeFromFavorites(recipe);
                        Toast.makeText(ViewMealDBRecipe.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // If not in favorites, add it
                        addRecipeToFavorites(recipe);
                        Toast.makeText(ViewMealDBRecipe.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    // Update UI based on whether recipe is favorite or not
                    updateFavoriteButtonUI(!isFavorite); // Invert favorite status
                } else {
                    // Handle errors
                    Toast.makeText(ViewMealDBRecipe.this, "Error checking favorite status: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to update UI of favoriteButton based on favorite status
    private void updateFavoriteButtonUI(boolean isFavorite) {
        favoriteButton.setChecked(isFavorite);
    }


    private void addRecipeToFavorites(MealDBRecipe recipe) {
        if (currentUser != null) {
            // Get the recipe ID from the MealDBRecipe
            int recipeId = recipe.getIDInt();

            // Convert the recipeId to a string
            String recipeIdString = String.valueOf(recipeId);

            // Construct the document reference using the recipe ID
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeIdString);

            String recipeName = recipe.getTitle();
            String image = recipe.getImageUrl();

            Map<String, Object> data = new HashMap<>();
            data.put("name", recipeName);
            data.put("imageURL", image);
            data.put("id", recipeIdString);
            data.put("mealDB", "1");

            // Set the document with the recipe data
            favoritesRef.set(data)
                    .addOnSuccessListener(aVoid -> {
                        // Recipe added successfully
                        Toast.makeText(ViewMealDBRecipe.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Toast.makeText(ViewMealDBRecipe.this, "Error adding to favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void removeRecipeFromFavorites(MealDBRecipe recipe) {
        if (currentUser != null) {
            // Get the recipe ID from the MealDBRecipe
            int recipeId = recipe.getIDInt();

            // Convert the recipeId to a string
            String recipeIdString = String.valueOf(recipeId);

            // Construct the document reference using the recipe ID
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeIdString);

            // Delete the document corresponding to the recipe ID
            favoritesRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Recipe removed successfully
                        Toast.makeText(ViewMealDBRecipe.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Toast.makeText(ViewMealDBRecipe.this, "Error removing from favorites", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}

