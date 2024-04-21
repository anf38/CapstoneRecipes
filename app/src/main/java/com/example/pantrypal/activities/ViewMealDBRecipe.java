package com.example.pantrypal.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.Comment;
import com.example.pantrypal.CommentsListAdapter;
import com.example.pantrypal.R;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewMealDBRecipe extends AppCompatActivity {
    private final RecipeRetriever recipeRetriever = new RecipeRetriever();
    private ImageView recipeImageView;
    private ListView commentsList;
    private ArrayList<Comment> commentsAL;
    private CommentsListAdapter adapter;
    private ImageView star1, star2, star3, star4, star5;
    private TextView ratingCount, commentCount;
    private Button reviewButton;
    private TextView recipeNameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private Button backButton;
    private MealDBRecipe recipe;
    String recipeId;
    private ToggleButton favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);

        recipe = (MealDBRecipe) getIntent().getSerializableExtra("recipe");
        new Thread(() -> {
            Bitmap recipeImage = recipeRetriever.getRecipeImage(recipe.getImageURL(), false);
            runOnUiThread(() -> recipeImageView.setImageBitmap(recipeImage));
            getRatings();
        }).start();


        // Initialize UI components
        recipeImageView = findViewById(R.id.imageView);
        recipeNameTextView = findViewById(R.id.recipeNameTextView);
        ingredientsTextView = findViewById(R.id.ingredientsTextView);
        instructionsTextView = findViewById(R.id.instructionsTextView);
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.favoriteButton);

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

        recipeNameTextView.setText(recipe.getTitle());
        ingredientsTextView.setText(formatIngredientsList(recipe.getIngredients()));
        instructionsTextView.setText(formatList(recipe.getInstructionLines()));

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipe.getId()!= null) {
                    recipeId = String.valueOf(recipe.getId());
                    Intent intent = new Intent(getApplicationContext(), FavoritesReviews.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                } else {
                    Toast.makeText(ViewMealDBRecipe.this, "Recipe ID is null", Toast.LENGTH_SHORT).show();
                }
            }
        });
        commentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the comment ID and content from the clicked item
                String commentId = commentsAL.get(position).getId();
                String commentTitle = commentsAL.get(position).getTitle();
                String commentMessage = commentsAL.get(position).getMessage();

                showReportDialog(commentId, commentTitle, commentMessage);
            }
        });
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavoriteStatus(recipe);
            }
        });

        // Set onClickListener for backButton to finish current activity and go back to previous

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to the previous one
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recheck the favorite status and update the UI
        if (recipe != null) {
            checkIfRecipeIsFavorite(recipe);
        }
    }

    private void checkIfRecipeIsFavorite(MealDBRecipe recipe) {
        if (currentUser != null) {
            String recipeID = String.valueOf(recipe.getId());
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeID);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isFavorite = task.getResult().exists();
                    // Update UI based on whether recipe is favorite or not
                    updateFavoriteButtonUI(isFavorite);
                } else {
                    // Handle errors
                    Toast.makeText(ViewMealDBRecipe.this, "Error checking favorite status: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        recipeRetriever.shutdown();
    }


    private String formatIngredientsList(List<String> ingredients) {
        StringBuilder ingredientQuantityList = new StringBuilder();
        for (String ingredient : ingredients) {
            ingredientQuantityList.append("- ").append(ingredient).append("\n");
        }

        return ingredientQuantityList.toString();
    }

    private void toggleFavoriteStatus(MealDBRecipe recipe) {
        int recipeId = recipe.getIDInt();

        // Convert the recipeId to a string
        String recipeIdString = String.valueOf(recipeId);

        // Check if the recipe is already in favorites
        if (currentUser != null) {
            DocumentReference favoritesRef = db.collection("users").document(currentUser.getUid()).collection("favorites").document(recipeIdString);

            favoritesRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean isFavorite = task.getResult().exists();
                    if (isFavorite) {
                        // If already in favorites, remove it

                        removeRecipeFromFavorites(recipe);
                        Toast.makeText(ViewMealDBRecipe.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        // If not in favorites, add it
                        addRecipeToFavorites(recipe);
                        Toast.makeText(ViewMealDBRecipe.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    }
                    // Update UI based on whether recipe is favorite or not
                    updateFavoriteButtonUI(!isFavorite); // Invert favorite status
                } else {
                    // Handle errors
                    Toast.makeText(ViewMealDBRecipe.this, "Error checking favorite status: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to update UI of favoriteButton based on favorite status
    private void updateFavoriteButtonUI(boolean isFavorite) {
        favoriteButton.setChecked(isFavorite);
    }


    private void addRecipeToFavorites(MealDBRecipe recipe) {
        if (currentUser != null) {
            // Get the recipe ID from the MealDBRecipe
            int recipeId = recipe.getIDInt();

            // Convert the recipeId to a string
            String recipeIdString = String.valueOf(recipeId);

            // Construct the document reference using the recipe ID
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeIdString);

            String recipeName = recipe.getTitle();
            String image = recipe.getImageURL();

            Map<String, Object> data = new HashMap<>();
            data.put("name", recipeName);
            data.put("imageURL", image);
            data.put("id", recipeIdString);
            data.put("mealDB", "1");

            // Set the document with the recipe data
            favoritesRef.set(data)
                    .addOnSuccessListener(aVoid -> {
                        // Recipe added successfully
                        Toast.makeText(ViewMealDBRecipe.this, "Added to favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Toast.makeText(ViewMealDBRecipe.this, "Error adding to favorites: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void removeRecipeFromFavorites(MealDBRecipe recipe) {
        if (currentUser != null) {
            // Get the recipe ID from the MealDBRecipe
            int recipeId = recipe.getIDInt();

            // Convert the recipeId to a string
            String recipeIdString = String.valueOf(recipeId);

            // Construct the document reference using the recipe ID
            DocumentReference favoritesRef = db.collection("users")
                    .document(currentUser.getUid())
                    .collection("favorites")
                    .document(recipeIdString);

            // Delete the document corresponding to the recipe ID
            favoritesRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Recipe removed successfully
                        Toast.makeText(ViewMealDBRecipe.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors
                        Toast.makeText(ViewMealDBRecipe.this, "Error removing from favorites", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void getRatings() {
        recipeId = recipe.getId();
        db.collection("mealDB").document(recipeId)
                .collection("ratings")
                .get()
                .addOnCompleteListener(task -> {
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
                            commentCount.setText(numberOfRatingsString);
                            String averageRatingString = String.format(Locale.US, "%.1f", averageRating);
                            ratingCount.setText(averageRatingString);
                            setStarRating(averageRating);
                            commentCount.setText(numberOfRatingsString);
                            ratingCount.setText(averageRatingString);
                            setStarRating(averageRating);
                        } else {
                            // Handle the case where there are no ratings yet
                            Log.d("Average Rating", "No ratings yet");
                        }
                    } else {
                        Log.d("Average Rating", "Error getting ratings: ", task.getException());
                    }
                });
    }

    private void setStarRating(double averageRating) {
        if (averageRating > 4.4) {
            setStars(R.drawable.full_star);
        } else if (averageRating > 3.4) {
            setStars(R.drawable.full_star);
            star5.setImageResource(R.drawable.empty_star);
        } else if (averageRating > 2.4) {
            setStars(R.drawable.full_star);
            star4.setImageResource(R.drawable.empty_star);
            star5.setImageResource(R.drawable.empty_star);
        } else if (averageRating > 1.4) {
            setStars(R.drawable.full_star);
            star3.setImageResource(R.drawable.empty_star);
            star4.setImageResource(R.drawable.empty_star);
            star5.setImageResource(R.drawable.empty_star);
        } else if (averageRating > 0.4) {
            setStars(R.drawable.full_star);
            star2.setImageResource(R.drawable.empty_star);
            star3.setImageResource(R.drawable.empty_star);
            star4.setImageResource(R.drawable.empty_star);
            star5.setImageResource(R.drawable.empty_star);
        } else {
            setStars(R.drawable.empty_star);
        }
    }

    private void setStars(int resourceId) {
        star1.setImageResource(resourceId);
        star2.setImageResource(resourceId);
        star3.setImageResource(resourceId);
        star4.setImageResource(resourceId);
        star5.setImageResource(resourceId);
    }

    private void showReportDialog(String commentId, String commentTitle, String commentMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Report Comment");
        builder.setMessage("Please select the reason for reporting:");

        // Inflate custom view for radio buttons
        View view = LayoutInflater.from(this).inflate(R.layout.report_dialog_layout, null);
        builder.setView(view);
        final RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = view.findViewById(selectedId);
                if (radioButton != null) {
                    String reason = radioButton.getText().toString();
                    submitReportToFirestore(commentId, commentTitle, commentMessage, reason);
                } else {
                    Toast.makeText(ViewMealDBRecipe.this, "Please select a reason for reporting.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    // Method to submit report to Firestore
    private void submitReportToFirestore(String commentId, String commentTitle, String commentMessage, String reason) {
        String currentUserID = currentUser.getUid();

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("commentId", commentId);
        reportData.put("commentTitle", commentTitle);
        reportData.put("commentMessage", commentMessage);
        reportData.put("reason", reason);
        reportData.put("reporterID", currentUserID);
        reportData.put("recipeId", recipeId);

        // Add the report to Firestore
        db.collection("reports")
                .add(reportData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ViewMealDBRecipe.this, "Report submitted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewMealDBRecipe.this, "Failed to submit report.", Toast.LENGTH_SHORT).show();
                    Log.e("ViewRecipe", "Error adding report to Firestore", e);
                });
    }

    private String formatList(List<String> list) {
        // Helper method to format a list of strings
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : list) {
            stringBuilder.append("- ").append(item).append("\n\n");
        }
        return stringBuilder.toString();
    }

}

