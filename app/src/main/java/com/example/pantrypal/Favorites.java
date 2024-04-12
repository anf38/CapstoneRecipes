package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrypal.apiTools.MealDBJSONParser;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter; // Changed from CustomAdapter to FavoritesAdapter
    private final RecipeRetriever recipeRetriever = new RecipeRetriever("capstone-recipes-server-a64f8333ac1b.herokuapp.com");

    private List<String> favoritesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recyclerView);
        favoritesList = new ArrayList<>();
        adapter = new FavoritesAdapter(this, favoritesList); // Changed from CustomAdapter to FavoritesAdapter
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (currentUser != null) {
            loadFavorites();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        adapter.setOnItemClickListener(new FavoritesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String item = favoritesList.get(position);
                String[] recipeData = item.split("\\|");
                String mealdb = recipeData[2]; // Assuming mealDBrecipe is at index 2
                if ("1".equals(mealdb)){
                    String recipeId = recipeData[1]; // Assuming recipeId is at index 1
                    JSONObject j = recipeRetriever.lookUp(Integer.parseInt(recipeId));
                    MealDBRecipe recipe = MealDBJSONParser.parseFirstRecipe(j);
                    if (recipe != null) {
                        Intent intent = new Intent(Favorites.this, ViewMealDBRecipe.class);
                        intent.putExtra("recipe", recipe);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Favorites.this, "Recipe details not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String recipeId = recipeData[0]; // Assuming recipe name is at index 0
                    Intent intent = new Intent(Favorites.this, ViewRecipe.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                }
            }
        });

    }

    private void loadFavorites() {
        db.collection("users").document(currentUser.getUid()).collection("favorites")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            favoritesList.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                String recipeName = document.getString("name");
                                String imageURL = document.getString("imageURL");
                                if (imageURL != null) {
                                    favoritesList.add(recipeName + "|" + imageURL);
                                } else {
                                    favoritesList.add(recipeName + "|");
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(Favorites.this, "Failed to load favorites: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
