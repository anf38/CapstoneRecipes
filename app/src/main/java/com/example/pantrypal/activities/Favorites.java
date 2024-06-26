package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pantrypal.FavoriteRecipe;
import com.example.pantrypal.FavoritesAdapter;
import com.example.pantrypal.R;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Favorites extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private RecipeRetriever recipeRetriever;
    private Button backButton;

    private List<FavoriteRecipe> favoritesList;
    FavoriteRecipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        recipeRetriever = RecipeRetriever.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);
        favoritesList = new ArrayList<>();
        adapter = new FavoritesAdapter(this, favoritesList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (currentUser != null) {
            loadFavorites();
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.setOnItemClickListener(new FavoritesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                recipe = favoritesList.get(position);
                String mealdb = recipe.getMealDB();
                if (mealdb.contentEquals("1")) {
                    if (recipeRetriever != null) {
                        String recipeId = recipe.getId();
                        Future<JSONObject> future = recipeRetriever.lookUpAsync(Integer.parseInt(recipeId));
                        try {
                            JSONObject j = future.get(); // This will block until the result is available
                            // Now you can proceed with using the JSONObject j
                            MealDBRecipe recipe = MealDBJSONParser.parseFirstRecipe(j);
                            if (recipe != null) {
                                Intent intent = new Intent(Favorites.this, ViewMealDBRecipe.class);
                                intent.putExtra("recipe", recipe);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Favorites.this, "Recipe details not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            // Handle any exceptions
                            e.printStackTrace();
                        }
                    }
                } else {
                    String recipeId = recipe.getId();
                    Intent intent = new Intent(Favorites.this, ViewRecipe.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFavorites();
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
                                String recipeId = document.getString("id");
                                String mealDB = document.getString("mealDB");
                                FavoriteRecipe recipe;
                                if (imageURL != null) {
                                    recipe = new FavoriteRecipe(recipeName, imageURL, recipeId, mealDB);
                                    favoritesList.add(recipe);
                                } else {
                                    imageURL = " ";
                                    recipe = new FavoriteRecipe(recipeName, imageURL, recipeId, mealDB);
                                    favoritesList.add(recipe);
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
