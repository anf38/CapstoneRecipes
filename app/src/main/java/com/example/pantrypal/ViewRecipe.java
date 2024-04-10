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
    private String recipeName;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private TextView instructionsTextView;
    private ImageView star1, star2, star3, star4, star5;
    private TextView tagsBox;
    private TextView ratingCount, commentCount;
    private Button reviewButton;
    private boolean isFavorite = false;

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
                toggleFavoriteStatus(recipeId);
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
    @Override
    protected void onResume() {
        super.onResume();
        // Recheck the favorite status and update the UI
        if (recipeId != null) {
            checkIfRecipeIsFavorite(recipeId);
        }
    }

    private void checkIfRecipeIsFavorite(String recipeId) {
        if (currentUser != null) {
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeId);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    isFavorite = task.getResult().exists();
                    // Update UI based on whether recipe is favorite or not
                    updateFavoriteButtonUI(isFavorite);
                } else {
                    // Handle errors
                    Log.e("ViewRecipe", "Error checking favorite status: ", task.getException());
                }
            });
        }
    }

    private void toggleFavoriteStatus(String recipeName) {
        // Check if the recipe is already in favorites
        if (currentUser != null) {
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeName);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isCurrentlyFavorite = task.getResult().exists();
                    if (isCurrentlyFavorite) {
                        // If already in favorites, remove it
                        removeRecipeFromFavorites(recipeName);
                        Toast.makeText(ViewRecipe.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // If not in favorites, add it
                        addRecipeToFavorites(recipeName);
                        Toast.makeText(ViewRecipe.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    // Update UI based on whether recipe is favorite or not
                    isFavorite = !isCurrentlyFavorite; // Invert favorite status
                    updateFavoriteButtonUI(isFavorite);
                } else {
                    // Handle errors
                    Log.e("ViewRecipe", "Error checking favorite status: ", task.getException());
                }
            });
        }
    }

    // Method to update UI of favoriteButton based on favorite status
    private void updateFavoriteButtonUI(boolean isFavorite) {
        favoriteButton.setChecked(isFavorite);
    }

    private void addRecipeToFavorites(String recipeId) {
        if (currentUser != null) {
            DocumentReference recipeRef = db.collection("recipes").document(recipeId);

            recipeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot recipeDoc = task.getResult();
                    if (recipeDoc.exists()) {
                        String recipeName = recipeDoc.getString("Name");

                        DocumentReference favoritesRef = db.collection("users")
                                .document(currentUser.getUid())
                                .collection("favorites")
                                .document(recipeId);

                        // Save the recipe ID as the name field in the favorites collection
                        Map<String, Object> data = new HashMap<>();
                        data.put("name", recipeName);

                        favoritesRef.set(data)
                                .addOnSuccessListener(aVoid -> {
                                    // Recipe added successfully
                                    Log.d("ViewRecipe", "Recipe added to favorites");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                    Log.e("ViewRecipe", "Error adding recipe to favorites", e);
                                });
                    } else {
                        Log.d("ViewRecipe", "Recipe document does not exist");
                    }
                } else {
                    Log.e("ViewRecipe", "Error getting recipe document", task.getException());
                }
            });
        }
    }



    private void removeRecipeFromFavorites(String recipeName) {
        if (currentUser != null) {
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeName);

            favoritesRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Recipe removed successfully
                        Log.d("ViewRecipe", "Recipe removed from favorites");
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Log.e("ViewRecipe", "Error removing recipe from favorites", e);
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
                                recipeName = document.getString("Name");
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
