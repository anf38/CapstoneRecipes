package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.apiTools.MealDBJSONParser;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ImageButton logoutBtn;
    BottomNavigationView nav;

    private final RecipeRetriever recipeRetriever = new RecipeRetriever("capstone-recipes-server-a64f8333ac1b.herokuapp.com");
    private final List<RecipeCard> newRecipeCards = new ArrayList<>();
    private final List<RecipeCard> recRecipeCards = new ArrayList<>();
    private final List<RecipeCard> trendRecipeCards = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        nav = findViewById(R.id.nav);
        nav.setSelectedItemId(R.id.homeIcon);
        //logoutBtn = findViewById(R.id.logoutIcon);

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

        trendRecipeCards.add(new RecipeCard(findViewById(R.id.firstTrendCard)));
        trendRecipeCards.add(new RecipeCard(findViewById(R.id.secondTrendCard)));
        trendRecipeCards.add(new RecipeCard(findViewById(R.id.thirdTrendCard)));
        trendRecipeCards.add(new RecipeCard(findViewById(R.id.fourthTrendCard)));
        trendRecipeCards.add(new RecipeCard(findViewById(R.id.fifthTrendCard)));
        trendRecipeCards.add(new RecipeCard(findViewById(R.id.sixthTrendCard)));

        new Thread(() -> {
            // TODO: Replace with recipe of the day
            JSONObject recipeOfTheDayJSON = recipeRetriever.randomRecipe(false);
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

            JSONObject moreRandomRecipesJSON = recipeRetriever.randomRecipe(true);
            List<MealDBRecipe> moreRandomRecipes = MealDBJSONParser.parseRecipes(moreRandomRecipesJSON);
            for (int i = 0; i < recRecipeCards.size() && i < moreRandomRecipes.size(); ++i) {
                trendRecipeCards.get(i).setRecipe(moreRandomRecipes.get(i));
            }

            runOnUiThread(() -> {
                recipeOfTheDayCard.setOnClickListener(MainActivity.this);

                for (RecipeCard newCard : newRecipeCards) {
                    newCard.setOnClickListener(MainActivity.this);
                }

                for (RecipeCard recCard : recRecipeCards) {
                    recCard.setOnClickListener(MainActivity.this);
                }

                for (RecipeCard trendCard : trendRecipeCards) {
                    trendCard.setOnClickListener(MainActivity.this);
                }
            });

        }).start();

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
                    return true;
                } else if (itemId == R.id.searchIcon) {
                    startActivity(new Intent(MainActivity.this, Search.class));
                    return true;
                } else if (itemId == R.id.favoriteIcon) {
                    Toast.makeText(MainActivity.this, "Favorite", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, Favorites.class));
                    return true;
                } else if (itemId == R.id.submissionIcon) {
                    startActivity(new Intent(MainActivity.this, NewRecipe.class));
                    return true;
                } else if (itemId == R.id.ingredientsIcon) {
                    Toast.makeText(MainActivity.this, "Ingredients", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, IngredientsSearch.class));
                    return true;
                }
                return false;
            }
        });
    }
}
