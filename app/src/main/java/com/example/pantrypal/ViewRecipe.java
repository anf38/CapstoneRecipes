package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ViewRecipe extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        // Initialize and set up the bottom navigation view
        BottomNavigationView bottomNavView = findViewById(R.id.nav);
        bottomNavView.setSelectedItemId(R.id.homeIcon); // Set the default selected item
        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeIcon) {
                    Intent intent = new Intent(ViewRecipe.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.searchIcon) {
                    startActivity(new Intent(ViewRecipe.this, Search.class));
                    finish();
                    return true;
                } else if (itemId == R.id.favoriteIcon) {
                    Toast.makeText(ViewRecipe.this, "Favorite", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ViewRecipe.this, Favorites.class));
                    finish();
                    return true;
                } else if (itemId == R.id.submissionIcon) {
                    startActivity(new Intent(ViewRecipe.this, NewRecipe.class));
                    finish();
                    return true;
                } else if (itemId == R.id.ingredientsIcon) {
                    Toast.makeText(ViewRecipe.this, "Ingredients", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ViewRecipe.this, IngredientsSearch.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);

        // Get recipe ID passed from previous activity
        String recipeId = getIntent().getStringExtra("recipeId");

        // Fetch and display recipe details
        fetchAndDisplayRecipeDetails(recipeId);
    }

    private void fetchAndDisplayRecipeDetails(String recipeId) {
        // Get reference to the recipe document
        db.collection("recipes").document(recipeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Extract recipe details from the document
                                String recipeName = document.getString("Name");
                                List<String> ingredients = (List<String>) document.get("Ingredients");
                                List<String> instructions = (List<String>) document.get("Instructions");

                                // Set the TextViews with the retrieved data
                                recipeNameTextView.setText(recipeName);
                                ingredientsTextView.setText(formatList(ingredients));
                                instructionsTextView.setText(formatList(instructions));
                            } else {
                                Log.d("ViewRecipe", "No such document");
                            }
                        } else {
                            Log.d("ViewRecipe", "get failed with ", task.getException());
                        }
                    }
                });
    }

    // Helper method to format a list of strings
    private String formatList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append("- ").append(item).append("\n");
        }
        return stringBuilder.toString();
    }
}
