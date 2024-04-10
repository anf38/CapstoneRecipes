package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRecipe extends AppCompatActivity {
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private TextView instructionsTextView;
    private ImageView star1, star2, star3, star4, star5;
    private TextView tagsBox;
    private TextView ratingCount, commentCount;
    private Button reviewButton;
    private String recipeId;
    private ListView commentsList;
    private ArrayList<Comment> commentsAL;
    private CommentsListAdapter adapter;
    private ToggleButton favoriteButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        favoriteButton = findViewById(R.id.favoriteButton);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        tagsBox = findViewById(R.id.tagsBox);
        reviewButton = findViewById(R.id.reviewButton);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        ratingCount = findViewById(R.id.ratingCount);
        commentCount = findViewById(R.id.commentCount);
        commentsList = findViewById(R.id.commentsList);
        commentsAL = new ArrayList<>();
        backButton = findViewById(R.id.backButton);

        adapter = new CommentsListAdapter(this, R.layout.comments_listview_layout, commentsAL);
        commentsList.setAdapter(adapter);

        // Get recipe ID passed from previous activity
        recipeId = getIntent().getStringExtra("recipeId");

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteStatus();
            }
        });


        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (recipeId != null) {
                    Intent intent = new Intent(getApplicationContext(), Reviews.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewRecipe.this, "Recipe ID is null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set onClickListener for backButton to finish current activity and go back to previous

        backButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
                finish(); // Close the current activity and return to the previous one
            }
        });

        // Get recipe ID passed from previous activity
        String recipeId = getIntent().getStringExtra("recipeId");


        fetchAndDisplayRecipeDetails(recipeId);
    }
    // Method to toggle favorite status of the recipe
    private void toggleFavoriteStatus() {
        // Check if the recipe is already in favorites
        if (currentUser != null) {
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeId);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isFavorite = task.getResult().exists();
                    if (isFavorite) {
                        // If already in favorites, remove it
                        removeRecipeFromFavorites();
                        Toast.makeText(ViewRecipe.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // If not in favorites, add it
                        addRecipeToFavorites();
                        Toast.makeText(ViewRecipe.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    // Update UI based on whether recipe is favorite or not
                    updateFavoriteButtonUI(!isFavorite); // Invert favorite status
                } else {
                    // Handle errors
                    Toast.makeText(ViewRecipe.this, "Error checking favorite status: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    // Method to update UI of favoriteButton based on favorite status
    private void updateFavoriteButtonUI(boolean isFavorite) {
        favoriteButton.setChecked(isFavorite);
    }

    // Add Recipe to Favorites
    private void addRecipeToFavorites() {
        if (currentUser != null && recipeId != null) {
            // Get reference to the recipe document
            DocumentReference recipeRef = db.collection("recipes").document(recipeId);

            // Get reference to the user's favorites collection
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeId);

            // Fetch the recipe name and store it in favorites
            recipeRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String recipeName = documentSnapshot.getString("Name");
                    if (recipeName != null && !recipeName.isEmpty()) {
                        // Create a Map to store the data in favorites
                        Map<String, Object> favoritesData = new HashMap<>();
                        favoritesData.put("name", recipeName);

                        // Set the data in favorites
                        favoritesRef.set(favoritesData)
                                .addOnSuccessListener(aVoid -> {
                                    // Recipe added successfully
                                    Log.d("ViewRecipe", "Recipe added to favorites");
                                    Toast.makeText(ViewRecipe.this, "Recipe added to favorites", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                    Log.e("ViewRecipe", "Error adding recipe to favorites", e);
                                    Toast.makeText(ViewRecipe.this, "Error adding recipe to favorites", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.d("ViewRecipe", "Recipe name is null or empty");
                    }
                } else {
                    Log.d("ViewRecipe", "Recipe document does not exist");
                }
            }).addOnFailureListener(e -> {
                // Handle errors
                Log.e("ViewRecipe", "Error fetching recipe document", e);
                Toast.makeText(ViewRecipe.this, "Error fetching recipe document", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Remove Recipe from Favorites
    private void removeRecipeFromFavorites() {
        if (currentUser != null) {
            // Get reference to the user's favorites collection
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeId);

            // Remove the recipe from favorites
            favoritesRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Recipe removed successfully
                        Log.d("ViewRecipe", "Recipe removed from favorites");
                        Toast.makeText(ViewRecipe.this, "Recipe removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Log.e("ViewRecipe", "Error removing recipe from favorites", e);
                        Toast.makeText(ViewRecipe.this, "Error removing recipe from favorites", Toast.LENGTH_SHORT).show();
                    });
        }
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
                                String tags = document.getString("Tags");
                                getRatings();

                                // Set the TextViews with the retrieved data
                                recipeNameTextView.setText(recipeName);
                                ingredientsTextView.setText(formatList(ingredients));
                                instructionsTextView.setText(formatList(instructions));
                                tagsBox.setText("Tags: " + tags);
                            } else {
                                Log.d("ViewRecipe", "No such document");
                            }
                        } else {
                            Log.d("ViewRecipe", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void getRatings() {
        Toast.makeText(this, "GetRatings called", Toast.LENGTH_SHORT).show();
        db.collection("recipes").document(recipeId)
                .collection("ratings")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            double totalRating = 0;
                            int numberOfRatings = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    double rating = document.getDouble("Rating");
                                    String ratingName = document.getString("ReviewTitle");
                                    String ratingText = document.getString("ReviewMessage");
                                    totalRating += rating;
                                    numberOfRatings++;
                                    Comment comment = new Comment(ratingName, ratingText);
                                    commentsAL.add(comment);
                                    adapter.notifyDataSetChanged();

                                }
                            }

                            if (numberOfRatings > 0) {
                                double averageRating = totalRating / numberOfRatings;
                                String numberOfRatingsString = String.valueOf(numberOfRatings);
                                String averageRatingString = String.valueOf(averageRating);
                                Toast.makeText(ViewRecipe.this, "rating: " + averageRatingString, Toast.LENGTH_SHORT).show();
                                commentCount.setText(numberOfRatingsString);
                                ratingCount.setText(averageRatingString);
                                if(averageRating > 4.4){
                                    star1.setImageResource(R.drawable.full_star);
                                    star2.setImageResource(R.drawable.full_star);
                                    star3.setImageResource(R.drawable.full_star);
                                    star4.setImageResource(R.drawable.full_star);
                                    star5.setImageResource(R.drawable.full_star);
                                } else if(averageRating > 3.4){
                                    star1.setImageResource(R.drawable.full_star);
                                    star2.setImageResource(R.drawable.full_star);
                                    star3.setImageResource(R.drawable.full_star);
                                    star4.setImageResource(R.drawable.full_star);
                                    star5.setImageResource(R.drawable.empty_star);
                                } else if(averageRating > 2.4){
                                    star1.setImageResource(R.drawable.full_star);
                                    star2.setImageResource(R.drawable.full_star);
                                    star3.setImageResource(R.drawable.full_star);
                                    star4.setImageResource(R.drawable.empty_star);
                                    star5.setImageResource(R.drawable.empty_star);
                                } else if(averageRating > 1.4){
                                    star1.setImageResource(R.drawable.full_star);
                                    star2.setImageResource(R.drawable.full_star);
                                    star3.setImageResource(R.drawable.empty_star);
                                    star4.setImageResource(R.drawable.empty_star);
                                    star5.setImageResource(R.drawable.empty_star);
                                } else if(averageRating > 0.4){
                                    star1.setImageResource(R.drawable.full_star);
                                    star2.setImageResource(R.drawable.empty_star);
                                    star3.setImageResource(R.drawable.empty_star);
                                    star4.setImageResource(R.drawable.empty_star);
                                    star5.setImageResource(R.drawable.empty_star);
                                } else {
                                    star1.setImageResource(R.drawable.empty_star);
                                    star2.setImageResource(R.drawable.empty_star);
                                    star3.setImageResource(R.drawable.empty_star);
                                    star4.setImageResource(R.drawable.empty_star);
                                    star5.setImageResource(R.drawable.empty_star);
                                }

                            } else {
                                // Handle the case where there are no ratings yet
                                Log.d("Average Rating", "No ratings yet");
                            }
                        } else {
                            Log.d("Average Rating", "Error getting ratings: ", task.getException());
                        }
                    }
                });
    }



    // Helper method to format a list of strings
    private String formatList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append("- ").append(item).append("\n\n");
        }
        return stringBuilder.toString();
    }
}
