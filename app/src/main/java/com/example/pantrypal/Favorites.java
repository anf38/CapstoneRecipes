package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Favorites extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ListView listView;
    private ArrayAdapter<String> adapter;
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
                ResultsRecipe clickedRecipe = (ResultsRecipe) parent.getItemAtPosition(position); // Get the clicked recipe
                String recipeId = clickedRecipe.getId(); // Get the ID of the clicked recipe

                Intent intent = new Intent(Favorites.this, ViewRecipe.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
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
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Favorites.this, "Failed to load favorites: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

