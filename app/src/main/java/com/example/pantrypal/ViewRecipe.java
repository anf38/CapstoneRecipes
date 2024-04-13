package com.example.pantrypal;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewRecipe extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe);


        //Initialize firebase user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to the previous one
            }
        });

        // Get recipe ID passed from previous activity
        String recipeId = getIntent().getStringExtra("recipeId");


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
                        data.put("id", recipeId);
                        data.put("mealDB", "0");

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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            // Extract recipe details from the document
                            String recipeName = task.getResult().getString("Name");
                            List<String> ingredients = (List<String>) task.getResult().get("Ingredients");
                            List<String> instructions = (List<String>) task.getResult().get("Instructions");
                            String tags = task.getResult().getString("Tags");
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
                });
    }

    private void getRatings() {
        db.collection("recipes").document(recipeId)
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
                            String averageRatingString = String.valueOf(averageRating);
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
                    Toast.makeText(ViewRecipe.this, "Please select a reason for reporting.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ViewRecipe.this, "Report submitted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ViewRecipe.this, "Failed to submit report.", Toast.LENGTH_SHORT).show();
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
