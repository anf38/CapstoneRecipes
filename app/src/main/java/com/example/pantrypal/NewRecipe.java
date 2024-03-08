package com.example.pantrypal;

import android.net.Uri;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NewRecipe extends AppCompatActivity {
    TextInputEditText recipeName, recipeIngredients, recipeInstructions;
    Button cancelRecipeButton, createRecipeButton, addTagsButton;
    FirebaseFirestore firestore;
    DatabaseReference databaseReference;
    CreateRecipe createRecipe;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newrecipe);
        firestore = FirebaseFirestore.getInstance();
        recipeName = findViewById(R.id.recipeName);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        cancelRecipeButton = findViewById(R.id.cancelRecipeButton);
        createRecipeButton = findViewById(R.id.createRecipeButton);
        progressBar = findViewById(R.id.progressBarSub);
        addTagsButton = findViewById(R.id.addTagsButton);

        createRecipe = new CreateRecipe();

        cancelRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Registration.class);
                startActivity(intent);
                finish();
            }
        });

        createRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                createRecipe.setName(String.valueOf(recipeName.getText()));
                createRecipe.setIngredients(String.valueOf(recipeIngredients.getText()).split("\n"));
                createRecipe.setInstructions(String.valueOf(recipeInstructions.getText()).split("\n"));

                String name = createRecipe.getName();
                String[] ingredients = createRecipe.getIngredients();
                String[] instructions = createRecipe.getInstructions();

                Map<String, Object> recipe = new HashMap<>();
                recipe.put("Name", name);
                recipe.put("Ingredients", Arrays.asList(ingredients)); // Convert to list
                recipe.put("Instructions", Arrays.asList(instructions)); // Convert to list

                firestore.collection("recipes").add(recipe)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(NewRecipe.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                progressBar.setVisibility(View.INVISIBLE);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(NewRecipe.this, "Failed to add recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}