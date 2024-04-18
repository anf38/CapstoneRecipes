package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.CommunityRecipeCard;
import com.example.pantrypal.R;
import com.example.pantrypal.apiTools.MealDBJSONParser;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ImageView logoutBtn;
    private BottomNavigationView nav;
    private FirebaseFirestore db;

    private final RecipeRetriever recipeRetriever = new RecipeRetriever();
    private final List<RecipeCard> newRecipeCards = new ArrayList<>();
    private final List<RecipeCard> recRecipeCards = new ArrayList<>();
    private final List<CommunityRecipeCard> communityRecipeCards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.homeIcon);
        logoutBtn = findViewById(R.id.logoutBtn);

        RecipeCard recipeOfTheDayCard = new RecipeCard(findViewById(R.id.recipeOfTheDay),
                findViewById(R.id.recipeOfTheDayImage),
                findViewById(R.id.recipeOfTheDayTitle));

        // Get references to all the CardViews (new)
        newRecipeCards.add(new RecipeCard(findViewById(R.id.firstNewCard)));
        newRecipeCards.add(new RecipeCard(findViewById(R.id.secondNewCard)));
        newRecipeCards.add(new RecipeCard(findViewById(R.id.thirdNewCard)));
        newRecipeCards.add(new RecipeCard(findViewById(R.id.fourthNewCard)));
        newRecipeCards.add(new RecipeCard(findViewById(R.id.fifthNewCard)));
        newRecipeCards.add(new RecipeCard(findViewById(R.id.sixthNewCard)));

        // TODO: Use Firebase to recommend & get trending recipes
        recRecipeCards.add(new RecipeCard(findViewById(R.id.firstRecCard)));
        recRecipeCards.add(new RecipeCard(findViewById(R.id.secondRecCard)));
        recRecipeCards.add(new RecipeCard(findViewById(R.id.thirdRecCard)));
        recRecipeCards.add(new RecipeCard(findViewById(R.id.fourthRecCard)));
        recRecipeCards.add(new RecipeCard(findViewById(R.id.fifthRecCard)));
        recRecipeCards.add(new RecipeCard(findViewById(R.id.sixthRecCard)));

        communityRecipeCards.add(new CommunityRecipeCard(findViewById(R.id.firstCommunityCard)));
        communityRecipeCards.add(new CommunityRecipeCard(findViewById(R.id.secondCommunityCard)));
        communityRecipeCards.add(new CommunityRecipeCard(findViewById(R.id.thirdCommunityCard)));
        communityRecipeCards.add(new CommunityRecipeCard(findViewById(R.id.fourthCommunityCard)));
        communityRecipeCards.add(new CommunityRecipeCard(findViewById(R.id.fifthCommunityCard)));
        communityRecipeCards.add(new CommunityRecipeCard(findViewById(R.id.sixthCommunityCard)));

        new Thread(() -> {
            JSONObject recipeOfTheDayJSON = recipeRetriever.getRecipeOfTheDay();
            MealDBRecipe recipeOfTheDay = MealDBJSONParser.parseFirstRecipe(recipeOfTheDayJSON);
            recipeOfTheDayCard.setRecipe(recipeOfTheDay);

            JSONObject latestRecipesJSON = recipeRetriever.latestRecipes();
            List<MealDBRecipe> latestRecipes = MealDBJSONParser.parseRecipes(latestRecipesJSON);
            for (int i = 0; i < newRecipeCards.size() && i < latestRecipes.size(); ++i) {
                newRecipeCards.get(i).setRecipe(latestRecipes.get(i));
            }

            JSONObject randomRecipesJSON = recipeRetriever.randomRecipe(true);
            List<MealDBRecipe> randomRecipes = MealDBJSONParser.parseRecipes(randomRecipesJSON);
            for (int i = 0; i < recRecipeCards.size() && i < randomRecipes.size(); ++i) {
                recRecipeCards.get(i).setRecipe(randomRecipes.get(i));
            }
            getRandomRecipeIds();

            runOnUiThread(() -> {
                recipeOfTheDayCard.setOnClickListener(MainActivity.this);

                for (RecipeCard newCard : newRecipeCards) {
                    newCard.setOnClickListener(MainActivity.this);
                }

                for (RecipeCard recCard : recRecipeCards) {
                    recCard.setOnClickListener(MainActivity.this);
                }

                for (CommunityRecipeCard communityCard : communityRecipeCards) {
                    communityCard.setOnClickListener(MainActivity.this);
                }
            });

        }).start();

// Set OnClickListener for other CardViews as needed


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeIcon) {
                    return true;
                } else if (itemId == R.id.searchIcon) {
                    startActivity(new Intent(MainActivity.this, Search.class));
                    return true;
                } else if (itemId == R.id.favoriteIcon) {
                    startActivity(new Intent(MainActivity.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.submissionIcon) {
                    startActivity(new Intent(MainActivity.this, NewRecipe.class));
                    return true;
                } else if (itemId == R.id.ingredientsIcon) {
                    startActivity(new Intent(MainActivity.this, IngredientsSearch.class));
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        recipeRetriever.shutdown();
    }


    private void getRandomRecipeIds() {
        db = FirebaseFirestore.getInstance();
        db.collection("recipes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> recipeIds = new ArrayList<>();
                    Random random = new Random();

                    // Iterate over the documents and add recipe IDs to the list
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        recipeIds.add(document.getId());
                    }

                    // Shuffle the list of recipe IDs
                    for (int i = 0; i < 6; i++) {
                        int index = random.nextInt(recipeIds.size());
                        String recipeId = recipeIds.get(index);
                        communityRecipeCards.get(i).setRecipe(recipeId);
                        recipeIds.remove(index); // To avoid duplicate IDs
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Log.e("MainActivity", "Error getting random recipe IDs", e);
                });
    }
}