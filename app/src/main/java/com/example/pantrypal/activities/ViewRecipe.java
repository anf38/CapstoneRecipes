package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.Comment;
import com.example.pantrypal.CommentsListAdapter;
import com.example.pantrypal.Favorites;
import com.example.pantrypal.NewRecipe;
import com.example.pantrypal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewRecipe extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private ImageView star1, star2, star3, star4, star5;
    private TextView tagsBox;
    private TextView ratingCount, commentCount;
    private Button reviewButton;
    private String recipeId;
    private ListView commentsList;
    private ArrayList<Comment> commentsAL;
    private CommentsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        // Initialize and set up the bottom navigation view
        BottomNavigationView bottomNavView = findViewById(R.id.nav);
        bottomNavView.setSelectedItemId(R.id.homeIcon); // Set the default selected item


        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        recipeNameTextView = findViewById(R.id.recipeNameTextView);
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

        adapter = new CommentsListAdapter(this, R.layout.comments_listview_layout, commentsAL);
        commentsList.setAdapter(adapter);

        // Get recipe ID passed from previous activity
        recipeId = getIntent().getStringExtra("recipeId");

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

        bottomNavView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeIcon) {
                    Intent intent = new Intent(ViewRecipe.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                    return true;
                } else if (itemId == R.id.searchIcon) {
                    startActivity(new Intent(ViewRecipe.this, Search.class));
                    finish();
                    return true;
                } else if (itemId == R.id.favoriteIcon) {
                    Toast.makeText(ViewRecipe.this, "Favorite", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ViewRecipe.this, Favorites.class));
                    finish();
                    return true;
                } else if (itemId == R.id.submissionIcon) {
                    startActivity(new Intent(ViewRecipe.this, NewRecipe.class));
                    finish();
                    return true;
                } else if (itemId == R.id.ingredientsIcon) {
                    Toast.makeText(ViewRecipe.this, "Ingredients", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ViewRecipe.this, IngredientsSearch.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

        fetchAndDisplayRecipeDetails(recipeId);
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
