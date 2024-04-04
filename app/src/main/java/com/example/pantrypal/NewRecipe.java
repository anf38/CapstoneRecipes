package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NewRecipe extends AppCompatActivity {
    private TextInputEditText recipeName, recipeIngredients, recipeInstructions;
    private Button cancelRecipeButton, createRecipeButton, addTagsButton;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private CreateRecipe createRecipe;
    private ProgressBar progressBar;
    private TextView tagsBox;
    private String author;

    private String name = "";
    private String[] ingredients = {};
    private String[] instructions = {};

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
        tagsBox = findViewById(R.id.tagsBox);

        createRecipe = new CreateRecipe();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // Retrieve recipe data from Intent extras
            name = extras.getString("name", "");
            instructions = extras.getStringArray("instructions");
            ingredients = extras.getStringArray("ingredients");
            ArrayList<String> tags = getIntent().getStringArrayListExtra("tags");


            recipeName.setText(name);
            String tagsText = TextUtils.join(", ", tags);

            tagsBox.setText(tagsText);

            if (ingredients != null && ingredients.length > 0) {
                recipeIngredients.setText(TextUtils.join("\n", ingredients));
            }
            if (instructions != null && instructions.length > 0) {
                recipeInstructions.setText(TextUtils.join("\n", instructions));
            }
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            author = currentUser.getUid();
        } else {
            Toast.makeText(this, "Alert: failed to get user", Toast.LENGTH_SHORT).show();
        }

        cancelRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

        addTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTags.class);
                // Assuming recipeName, recipeIngredients, and recipeInstructions are EditText views
                name = recipeName.getText().toString();
                String ingredientsText = recipeIngredients.getText().toString();
                String instructionsText = recipeInstructions.getText().toString();
                ingredients = ingredientsText.split("\n");
                instructions = instructionsText.split("\n");

                intent.putExtra("name", name);
                intent.putExtra("instructions", instructions);
                intent.putExtra("ingredients", ingredients);
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
                String tagsText = tagsBox.getText().toString();

                if (name.isEmpty() || ingredients[0].isEmpty() || instructions[0].isEmpty()) {
                    Toast.makeText(NewRecipe.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                Map<String, Object> recipe = new HashMap<>();
                recipe.put("Name", name);
                recipe.put("Ingredients", Arrays.asList(ingredients));
                recipe.put("Instructions", Arrays.asList(instructions));
                recipe.put("Tags", tagsText);
                recipe.put("Author", author);


                firestore.collection("recipes").add(recipe).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(NewRecipe.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        progressBar.setVisibility(View.INVISIBLE);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
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