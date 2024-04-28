package com.example.pantrypal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.CreateRecipe;
import com.example.pantrypal.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NewRecipe extends AppCompatActivity {
    private TextInputEditText recipeName, recipeIngredients, recipeInstructions;
    private Button cancelRecipeButton;
    private Button createRecipeButton;
    private Button addTagsButton;
    private Button selectImageButton;
    private FirebaseFirestore firestore;
    private DatabaseReference databaseReference;
    private CreateRecipe createRecipe;
    private ProgressBar progressBar;
    private TextView tagsBox;
    private String author;
    private ImageView imageView;
    private Uri imageUri;
    private String name = "";
    private String[] ingredients = {};
    private String[] instructions = {};

    private ActivityResultLauncher<String> imagePickerLauncher;

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
        imageView = findViewById(R.id.imageView);
        selectImageButton = findViewById(R.id.selectImageButton);

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
                finish();
            }
        });

        addTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddTags.class);
                name = recipeName.getText().toString();
                String ingredientsText = recipeIngredients.getText().toString();
                String instructionsText = recipeInstructions.getText().toString();
                ingredients = ingredientsText.split("\n");
                instructions = instructionsText.split("\n");

                intent.putExtra("name", name);
                intent.putExtra("instructions", instructions);
                intent.putExtra("ingredients", ingredients);
                startActivity(intent);
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickerLauncher.launch("image/*");
            }
        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    imageUri = result;
                    imageView.setImageURI(imageUri);
                }
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

                // Continue with recipe creation including image upload if imageUri is not null
                if (imageUri != null) {
                    // Upload image to Firebase Storage and then create recipe in Firestore
                    uploadImageAndCreateRecipe(name, ingredients, instructions, tagsText);
                } else {
                    // Create recipe in Firestore without image
                    createRecipeInFirestore(name, ingredients, instructions, tagsText, null);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> tags = getIntent().getStringArrayListExtra("tags");
            if (tags != null) {
                String tagsText = TextUtils.join(", ", tags);
                tagsBox.setText(tagsText);
            }
        }
    }

    private void uploadImageAndCreateRecipe(String name, String[] ingredients, String[] instructions, String tagsText) {
        // Storage reference path where the image will be uploaded
        String storagePath = "recipe_images/" + System.currentTimeMillis() + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);

        // Upload image to Firebase Storage
        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            // Image upload successful, get the download URL
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                // Create recipe in Firestore with image URL
                createRecipeInFirestore(name, ingredients, instructions, tagsText, imageUrl);
            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(NewRecipe.this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(NewRecipe.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void createRecipeInFirestore(String name, String[] ingredients, String[] instructions, String tagsText, String imageUrl) {
        Map<String, Object> recipe = new HashMap<>();
        recipe.put("Name", name);
        recipe.put("Ingredients", Arrays.asList(ingredients));
        recipe.put("Instructions", Arrays.asList(instructions));
        recipe.put("Tags", tagsText);
        recipe.put("Author", author);
        if (imageUrl != null) {
            recipe.put("ImageUrl", imageUrl);
        }

        firestore.collection("recipes").add(recipe).addOnSuccessListener(documentReference -> {
            Toast.makeText(NewRecipe.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
            // Navigate to main activity or do something else
            progressBar.setVisibility(View.INVISIBLE);
            finish(); // Finish current activity if needed
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(NewRecipe.this, "Failed to add recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}