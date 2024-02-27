package com.example.pantrypal;

import android.net.Uri;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewRecipe extends AppCompatActivity {
    TextInputEditText recipeName, recipeIngredients, recipeInstructions;
    Button cancelRecipeButton, createRecipeButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    CreateRecipe createRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newrecipe);

        recipeName = findViewById(R.id.recipeName);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipeInstructions = findViewById(R.id.recipeInstructions);
        cancelRecipeButton = findViewById(R.id.cancelRecipeButton);
        createRecipeButton = findViewById(R.id.createRecipeButton);

        createRecipe = new CreateRecipe();
        firebaseDatabase = FirebaseDatabase.getInstance();//used to get the instance of our Firebase database.
        databaseReference = firebaseDatabase.getReference("server/saving-data/fireblog");

        cancelRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("create button works????");
                createRecipe.setName(String.valueOf(recipeName.getText()));
                createRecipe.setIngredients(String.valueOf(recipeIngredients.getText()).split("\n"));
                createRecipe.setInstructions(String.valueOf(recipeInstructions.getText()).split("\n"));
                System.out.println("setting information works");

                String name = createRecipe.getName();
                String[] ingredients = createRecipe.getIngredients();
                String[] instructions = createRecipe.getInstructions();
                Uri picture = createRecipe.getPictureUri();

                addDataToFirebase(name, ingredients, instructions, picture);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addDataToFirebase(String name, String[] ingredients, String[] instructions, Uri picture) {

        createRecipe.setName(name);
        createRecipe.setIngredients(ingredients);
        createRecipe.setInstructions(instructions);
        createRecipe.setPictureUri(picture);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user's ID

            // Get reference to the current user's trip
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Trips").push();

            databaseReference.setValue(createRecipe)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(NewRecipe.this, "recipe added successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NewRecipe.this, "Failed to add recipe: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}
