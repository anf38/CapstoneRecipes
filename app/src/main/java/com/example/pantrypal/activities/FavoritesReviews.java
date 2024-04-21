package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;
import com.example.pantrypal.apiTools.MealDBJSONParser;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FavoritesReviews extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView reviewImageHeader;
    private TextView reviewHeader;
    private ImageView star1, star2, star3, star4, star5;
    private ImageView reviewImage;
    private final RecipeRetriever recipeRetriever = new RecipeRetriever();

    private TextInputEditText reviewTitle, reviewMessage;
    private Button cancelReviewButton, submitReviewButton;
    private String recipeId, user;
    private int rating = 0;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        Toast.makeText(this, "c: " + currentUser.getUid(), Toast.LENGTH_SHORT).show();
        db = FirebaseFirestore.getInstance();

        reviewImageHeader = findViewById(R.id.reviewImageHeader);
        reviewHeader = findViewById(R.id.reviewHeader);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        reviewImage = findViewById(R.id.reviewImage);
        reviewTitle = findViewById(R.id.reviewTitle);
        reviewMessage = findViewById(R.id.reviewMessage);
        cancelReviewButton = findViewById(R.id.cancelReviewButton);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        recipeId = getIntent().getStringExtra("recipeId");


        cancelReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating != 0) {
                    // Check if the user has already rated the recipe
                    db.collection("mealDB").document(recipeId)
                            .collection("ratings")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        boolean alreadyRated = false;
                                        String ratingId = null;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            String rater = document.getString("Rater");
                                            if (rater != null && rater.equals(currentUser.getUid())) {
                                                Toast.makeText(FavoritesReviews.this, "this was triggerd true", Toast.LENGTH_SHORT).show();
                                                alreadyRated = true;
                                                ratingId = document.getId();
                                                break;
                                            }
                                        }

                                        if (alreadyRated) {
                                            // Update existing rating
                                            Map<String, Object> updateData = new HashMap<>();
                                            updateData.put("Rating", rating);
                                            String title = reviewTitle.getText().toString();
                                            String message = reviewMessage.getText().toString();
                                            if (!title.isEmpty())
                                                updateData.put("ReviewTitle", title);
                                            if (!message.isEmpty())
                                                updateData.put("ReviewMessage", message);

                                            db.collection("mealDB").document(recipeId)
                                                    .collection("ratings").document(ratingId)
                                                    .update(updateData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(FavoritesReviews.this, "Rating updated successfully", Toast.LENGTH_SHORT).show();
                                                            Future<JSONObject> future = recipeRetriever.lookUpAsync(Integer.parseInt(recipeId));
                                                            try {
                                                                JSONObject j = future.get(); // This will block until the result is available
                                                                // Now you can proceed with using the JSONObject j
                                                                MealDBRecipe recipe = MealDBJSONParser.parseFirstRecipe(j);
                                                                if (recipe != null) {
                                                                    Intent intent = new Intent(FavoritesReviews.this, ViewMealDBRecipe.class);
                                                                    intent.putExtra("recipe", recipe);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Toast.makeText(FavoritesReviews.this, "Recipe details not found", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (InterruptedException | ExecutionException e) {
                                                                // Handle any exceptions
                                                                e.printStackTrace();
                                                            }
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(FavoritesReviews.this, "Failed to update rating", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // Add new rating
                                            Map<String, Object> ratingData = new HashMap<>();
                                            ratingData.put("Rater", currentUser.getUid());
                                            ratingData.put("Rating", rating);
                                            String title = reviewTitle.getText().toString();
                                            String message = reviewMessage.getText().toString();
                                            if (!title.isEmpty())
                                                ratingData.put("ReviewTitle", title);
                                            else
                                            if (!message.isEmpty())
                                                ratingData.put("ReviewMessage", message);

                                            db.collection("mealDB").document(recipeId)
                                                    .collection("ratings")
                                                    .add(ratingData)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Future<JSONObject> future = recipeRetriever.lookUpAsync(Integer.parseInt(recipeId));
                                                            try {
                                                                JSONObject j = future.get(); // This will block until the result is available
                                                                // Now you can proceed with using the JSONObject j
                                                                MealDBRecipe recipe = MealDBJSONParser.parseFirstRecipe(j);
                                                                if (recipe != null) {
                                                                    Intent intent = new Intent(FavoritesReviews.this, ViewMealDBRecipe.class);
                                                                    intent.putExtra("recipe", recipe);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Toast.makeText(FavoritesReviews.this, "Recipe details not found", Toast.LENGTH_SHORT).show();
                                                                }
                                                            } catch (InterruptedException | ExecutionException e) {
                                                                // Handle any exceptions
                                                                e.printStackTrace();
                                                            }
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(FavoritesReviews.this, "Failed to add rating", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    } else {
                                        Toast.makeText(FavoritesReviews.this, "Failed to check user rating", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(FavoritesReviews.this, "You cannot rate the recipe with 0 stars", Toast.LENGTH_SHORT).show();
                }
            }
        });


        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.full_star);
                star2.setImageResource(R.drawable.empty_star);
                star3.setImageResource(R.drawable.empty_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                rating = 1;
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.full_star);
                star2.setImageResource(R.drawable.full_star);
                star3.setImageResource(R.drawable.empty_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                rating = 2;
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.full_star);
                star2.setImageResource(R.drawable.full_star);
                star3.setImageResource(R.drawable.full_star);
                star4.setImageResource(R.drawable.empty_star);
                star5.setImageResource(R.drawable.empty_star);
                rating = 3;
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.full_star);
                star2.setImageResource(R.drawable.full_star);
                star3.setImageResource(R.drawable.full_star);
                star4.setImageResource(R.drawable.full_star);
                star5.setImageResource(R.drawable.empty_star);
                rating = 4;
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.drawable.full_star);
                star2.setImageResource(R.drawable.full_star);
                star3.setImageResource(R.drawable.full_star);
                star4.setImageResource(R.drawable.full_star);
                star5.setImageResource(R.drawable.full_star);
                rating = 5;
            }
        });
    }

}
