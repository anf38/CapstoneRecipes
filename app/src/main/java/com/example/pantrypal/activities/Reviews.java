package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Reviews extends AppCompatActivity {
    private FirebaseFirestore db;
    private ImageView reviewImageHeader;
    private TextView reviewHeader;
    private ImageView star1, star2, star3, star4, star5;
    private ImageView reviewImage;
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
        fetchAndDisplayRecipeDetails(recipeId);

        cancelReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ViewRecipe.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
                finish();
            }
        });

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rating != 0) {
                    // Check if the user has already rated the recipe
                    db.collection("recipes").document(recipeId)
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

                                            db.collection("recipes").document(recipeId)
                                                    .collection("ratings").document(ratingId)
                                                    .update(updateData)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(Reviews.this, "Rating updated successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), ViewRecipe.class);
                                                            intent.putExtra("recipeId", recipeId);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Reviews.this, "Failed to update rating", Toast.LENGTH_SHORT).show();
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
                                            if (!message.isEmpty())
                                                ratingData.put("ReviewMessage", message);

                                            db.collection("recipes").document(recipeId)
                                                    .collection("ratings")
                                                    .add(ratingData)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(Reviews.this, "Rating added successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), ViewRecipe.class);
                                                            intent.putExtra("recipeId", recipeId);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(Reviews.this, "Failed to add rating", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    } else {
                                        Toast.makeText(Reviews.this, "Failed to check user rating", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Reviews.this, "You cannot rate the recipe with 0 stars", Toast.LENGTH_SHORT).show();
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

    private void fetchAndDisplayRecipeDetails(String recipeId) {
        db.collection("recipes").document(recipeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String recipeName = document.getString("Name");
                                //get recipe image from here
                                user = document.getString("Author");
                            } else
                                Toast.makeText(Reviews.this, "Couldn't find recipe", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(Reviews.this, "Couldn't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
