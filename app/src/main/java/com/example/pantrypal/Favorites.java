package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Favorites extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListView listView;
    private CustomAdapter adapter;

    private List<String> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        listView = findViewById(R.id.listView);
        favoritesList = new ArrayList<>();
        adapter = new CustomAdapter(this, R.layout.favorite_list_item, favoritesList);
        listView.setAdapter(adapter);

        if (currentUser != null) {
            loadFavorites();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String recipeNameAndImageURL = favoritesList.get(position); // Get the clicked item as string
                String[] parts = recipeNameAndImageURL.split("\\|");
                String recipeName = parts[0];

                getRecipeIdFromName(recipeName).thenAccept(recipeId -> {
                    if (recipeId != null) {
                        // Use the recipeId here
                        Intent intent = new Intent(Favorites.this, ViewRecipe.class);
                        intent.putExtra("recipeId", recipeId);
                        startActivity(intent);
                    } else {
                        // Handle the case where no matching recipe is found
                        Toast.makeText(Favorites.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void loadFavorites() {
        db.collection("users").document(currentUser.getUid()).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        favoritesList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            String recipeName = document.getString("name"); // Assuming "name" is the field storing recipe names
                            String imageURL = document.getString("imageURL");
                            if (imageURL != null) {
                                favoritesList.add(recipeName + "|" + imageURL);
                            } else {
                                favoritesList.add(recipeName + "|");
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter that data set has changed
                    } else {
                        Toast.makeText(Favorites.this, "Failed to load favorites: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private CompletableFuture<String> getRecipeIdFromName(String recipeName) {
        CompletableFuture<String> future = new CompletableFuture<>();

        // Reference to the Firestore collection containing recipes
        db.collection("recipes")
                .whereEqualTo("Name", recipeName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Loop through the query results
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the recipe ID from the document
                            String recipeId = document.getId();
                            future.complete(recipeId); // Complete the CompletableFuture with the recipe ID
                            return;
                        }
                        // If no matching recipe is found
                        future.complete(null);
                    } else {
                        // Handle errors
                        Log.e("Favorites", "Error getting documents: ", task.getException());
                        // Complete the CompletableFuture with null to indicate failure
                        future.complete(null);
                    }
                });

        return future;
    }
}
