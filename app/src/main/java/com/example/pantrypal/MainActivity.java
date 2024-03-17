package com.example.pantrypal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ImageButton logoutBtn;
    BottomNavigationView nav;
    RecipeRetriever recipeRetriever = new RecipeRetriever("192.168.12.196", 8080);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.homeIcon);
        //logoutBtn = findViewById(R.id.logoutIcon);

        // Put image in daily recipe view
        ImageView dailyRecipeView = findViewById(R.id.imageView);
        Bitmap dailyRecipeImage = recipeRetriever.getRecipeImage(52772, false);
        if (dailyRecipeImage != null)
            dailyRecipeView.setImageBitmap(dailyRecipeImage);

// Get references to all the CardViews (new)
        CardView firstNewCard = findViewById(R.id.firstNewCard);
        CardView secondNewCard = findViewById(R.id.secondNewCard);
        CardView thirdNewCard = findViewById(R.id.thirdNewCard);
        CardView fourthNewCard = findViewById(R.id.fourthNewCard);
        CardView fifthNewCard = findViewById(R.id.fifthNewCard);
        CardView sixthNewCard = findViewById(R.id.sixthNewCard);
        // Get references to all the CardViews (recommended)
        CardView firstRecCard = findViewById(R.id.firstRecCard);
        CardView secondRecCard = findViewById(R.id.secondRecCard);
        CardView thirdRecCard = findViewById(R.id.thirdRecCard);
        CardView fourthRecCard = findViewById(R.id.fourthRecCard);
        CardView fifthRecCard = findViewById(R.id.fifthRecCard);
        CardView sixthRecCard = findViewById(R.id.sixthRecCard);
        // Get references to all the CardViews (Trending)
        CardView firstTrendCard = findViewById(R.id.firstTrendCard);
        CardView secondTrendCard = findViewById(R.id.secondTrendCard);
        CardView thirdTrendCard = findViewById(R.id.thirdTrendCard);
        CardView fourthTrendCard = findViewById(R.id.fourthTrendCard);
        CardView fifthTrendCard = findViewById(R.id.fifthTrendCard);
        CardView sixthTrendCard = findViewById(R.id.sixthTrendCard);

// Create an OnClickListener for all CardViews
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action when any CardView is clicked, such as navigating to a new activity
                Intent intent = new Intent(MainActivity.this, ViewRecipe.class);
                startActivity(intent);
                finish();
            }
        };

// Set the same OnClickListener to all CardViews
        firstNewCard.setOnClickListener(cardClickListener);
        secondNewCard.setOnClickListener(cardClickListener);
        thirdNewCard.setOnClickListener(cardClickListener);
        fourthNewCard.setOnClickListener(cardClickListener);
        fifthNewCard.setOnClickListener(cardClickListener);
        sixthNewCard.setOnClickListener(cardClickListener);

        firstRecCard.setOnClickListener(cardClickListener);
        secondRecCard.setOnClickListener(cardClickListener);
        thirdRecCard.setOnClickListener(cardClickListener);
        fourthRecCard.setOnClickListener(cardClickListener);
        fifthRecCard.setOnClickListener(cardClickListener);
        sixthRecCard.setOnClickListener(cardClickListener);

        firstTrendCard.setOnClickListener(cardClickListener);
        secondTrendCard.setOnClickListener(cardClickListener);
        thirdTrendCard.setOnClickListener(cardClickListener);
        fourthTrendCard.setOnClickListener(cardClickListener);
        fifthTrendCard.setOnClickListener(cardClickListener);
        sixthTrendCard.setOnClickListener(cardClickListener);

// Set OnClickListener for other CardViews as needed


//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeIcon) {
                    Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_LONG).show();
                    // Handle navigation to Home activity if necessary
                    return true;
                } else if (itemId == R.id.searchIcon) {
                    Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_LONG).show();
                    // Handle navigation to Search activity
                    startActivity(new Intent(MainActivity.this, Search.class));
                    return true;
                } else if (itemId == R.id.favoriteIcon) {
                    Toast.makeText(MainActivity.this, "Favorite", Toast.LENGTH_LONG).show();
                    // Handle navigation to Favorite activity
                    startActivity(new Intent(MainActivity.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.submissionIcon) {
                    Toast.makeText(MainActivity.this, "Submission", Toast.LENGTH_LONG).show();
                    // Handle navigation to Submission activity
                    startActivity(new Intent(MainActivity.this, NewRecipe.class));
                    return true;
                } else if (itemId == R.id.ingredientsIcon) {
                    Toast.makeText(MainActivity.this, "Ingredients", Toast.LENGTH_LONG).show();
                    // Handle navigation to Ingredients activity
                    startActivity(new Intent(MainActivity.this, IngredientsSearch.class));
                    return true;
                }
                return false;
            }
        });
    }
}
