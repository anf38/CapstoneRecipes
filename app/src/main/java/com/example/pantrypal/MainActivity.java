package com.example.pantrypal;
import android.content.Intent;
import android.content.om.FabricatedOverlay;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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





        //goal: make it so that the screen outputs the top 4 most recently created recipes









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
