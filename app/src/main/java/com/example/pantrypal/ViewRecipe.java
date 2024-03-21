package com.example.pantrypal;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
