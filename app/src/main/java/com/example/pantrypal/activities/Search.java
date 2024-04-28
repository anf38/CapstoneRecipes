package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;
import com.example.pantrypal.ResultsRecipe;
import com.example.pantrypal.SearchListAdapter;
import com.example.pantrypal.apiTools.MealDBJSONParser;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Search extends AppCompatActivity {
    private static final String TAG = "SEARCH";
    private FirebaseFirestore fStore;
    private RecipeRetriever apiRecipeRetriever;
    private Button cancelButton;
    private ListView listView;
    private SearchListAdapter searchListAdapter;
    private final ArrayList<ResultsRecipe> communityRecipes = new ArrayList<>();
    private final ArrayList<MealDBRecipe> apiRecipes = new ArrayList<>();

    private List<ResultsRecipe> resultRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fStore = FirebaseFirestore.getInstance();
        apiRecipeRetriever = RecipeRetriever.getInstance();

        listView = findViewById(R.id.listView);
        searchListAdapter = new SearchListAdapter(this, R.layout.list_item_recipe, new ArrayList<>());
        listView.setAdapter(searchListAdapter);
        cancelButton = findViewById(R.id.backButton);

        fetchRecipesFromFirestore();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) instanceof MealDBRecipe) {
                    MealDBRecipe apiRecipe = (MealDBRecipe) parent.getItemAtPosition(position);
                    Intent intent = new Intent(Search.this, ViewMealDBRecipe.class);
                    intent.putExtra("recipe", apiRecipe);
                    startActivity(intent);
                } else {
                    ResultsRecipe clickedRecipe = (ResultsRecipe) parent.getItemAtPosition(position); // Get the clicked recipe
                    String recipeId = clickedRecipe.getId(); // Get the ID of the clicked recipe
                    Intent intent = new Intent(Search.this, ViewRecipe.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                }
            }
        });

    }

    private void fetchRecipesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String recipeName = document.getString("Name");
                        String recipeId = document.getId(); // Get recipe document ID
                        String recipeImage = document.getString("ImageUrl");
                        List<String> recipeIngredients = (List<String>) document.get("Ingredients");
                        ResultsRecipe resultsRecipe = new ResultsRecipe(recipeName, recipeId, recipeIngredients, recipeImage);
                        // Populate the original list as well
                        communityRecipes.add(resultsRecipe);
                        resultRecipes.add(resultsRecipe);
                    }
                    updateList();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listView.setVisibility(View.VISIBLE);
                searchView.clearFocus();

                new Thread(() -> {
                    // find recipes by name
                    List<MealDBRecipe> foundRecipes;
                    foundRecipes = MealDBJSONParser.parseRecipes(apiRecipeRetriever.searchByName(query));
                    if (foundRecipes != null) apiRecipes.addAll(foundRecipes);

                    runOnUiThread(() -> {
                        filterRecipes(query);
                        updateList();
                    });
                }, "APIRecipeSearch").start();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search query is empty, restore the original list
                    listView.setVisibility(View.GONE);
                    apiRecipes.clear();
                    resultRecipes.clear();
                    resultRecipes.addAll(communityRecipes);
                    updateList();
                } else {
                    // Filter the recipes based on the search query
                    filterRecipes(newText);
                    updateList();
                }
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterRecipes(String filterText) {
        List<ResultsRecipe> combinedList = (List<ResultsRecipe>) communityRecipes.clone();
        combinedList.addAll(apiRecipes);

        resultRecipes = combinedList.parallelStream().filter(recipe -> {
            String[] words = filterText.split("\\W+");

            for (String word : words)
                if (recipe.getTitle().toLowerCase().contains(word.toLowerCase())) return true;

            return false;
        }).collect(Collectors.toList());
    }

    private void updateList() {
        searchListAdapter.clear();
        searchListAdapter.addAll(resultRecipes);
    }
}
