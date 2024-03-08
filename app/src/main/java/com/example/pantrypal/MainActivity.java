package com.example.pantrypal;
import android.content.Intent;
import android.content.om.FabricatedOverlay;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.homeIcon);

        // Inside your activity or fragment

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
// Add references to other CardViews as needed

// Create an OnClickListener for all CardViews
        View.OnClickListener cardClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform action when any CardView is clicked, such as navigating to a new activity
                Intent intent = new Intent(MainActivity.this, ViewRecipe.class);
                startActivity(intent);
            }
        };

// Set the same OnClickListener to all CardViews
        firstNewCard.setOnClickListener(cardClickListener);
        secondNewCard.setOnClickListener(cardClickListener);
        thirdNewCard.setOnClickListener(cardClickListener);
        fourthNewCard.setOnClickListener(cardClickListener);
        fifthNewCard.setOnClickListener(cardClickListener);
        sixthNewCard.setOnClickListener(cardClickListener);

        firstNewCard.setOnClickListener(cardClickListener);
        secondNewCard.setOnClickListener(cardClickListener);
        thirdNewCard.setOnClickListener(cardClickListener);
        fourthNewCard.setOnClickListener(cardClickListener);
        fifthNewCard.setOnClickListener(cardClickListener);
        sixthNewCard.setOnClickListener(cardClickListener);

        firstNewCard.setOnClickListener(cardClickListener);
        secondNewCard.setOnClickListener(cardClickListener);
        thirdNewCard.setOnClickListener(cardClickListener);
        fourthNewCard.setOnClickListener(cardClickListener);
        fifthNewCard.setOnClickListener(cardClickListener);
        sixthNewCard.setOnClickListener(cardClickListener);
// Set OnClickListener for other CardViews as needed







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
