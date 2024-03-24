package com.example.pantrypal;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ViewRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewrecipe); // Set the layout file for this activity

        // Initialize and set up the bottom navigation view
        BottomNavigationView bottomNavView = findViewById(R.id.nav);
        bottomNavView.setSelectedItemId(R.id.homeIcon); // Set the default selected item

        bottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.homeIcon) {
                    Toast.makeText(ViewRecipe.this, "Home", Toast.LENGTH_LONG).show();
                    return true;
                } else if (item.getItemId() == R.id.searchIcon) {
                    Toast.makeText(ViewRecipe.this, "Search", Toast.LENGTH_LONG).show();
                    // startActivity(new Intent(ViewRecipe.this, Search.class));
                    return true;
                } else if (item.getItemId() == R.id.favoriteIcon) {
                    Toast.makeText(ViewRecipe.this, "Favorite", Toast.LENGTH_LONG).show();
                    // startActivity(new Intent(ViewRecipe.this, Favorites.class));
                    return true;
                } else if (item.getItemId() == R.id.submissionIcon) {
                    Toast.makeText(ViewRecipe.this, "Submission", Toast.LENGTH_LONG).show();
                    // startActivity(new Intent(ViewRecipe.this, NewRecipe.class));
                    return true;
                } else if (item.getItemId() == R.id.ingredientsIcon) {
                    Toast.makeText(ViewRecipe.this, "Ingredients", Toast.LENGTH_LONG).show();
                    // startActivity(new Intent(ViewRecipe.this, IngredientsSearch.class));
                    return true;
                }
                return false;
            }
        });
    }
}
