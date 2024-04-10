package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;
import com.example.pantrypal.ResultsRecipe;
import com.example.pantrypal.SearchListAdapter;
import com.example.pantrypal.apiTools.MealDBRecipe;
import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private static final String TAG = "SEARCH";
    private FirebaseFirestore fStore;
    private ListView listView;
    private SearchListAdapter searchListAdapter;
    private final ArrayList<ResultsRecipe> originalRecipeList = new ArrayList<>();
    private final ArrayList<ResultsRecipe> filteredRecipeList = new ArrayList<>();

    private final RecipeRetriever apiRecipeRetriever = new RecipeRetriever();
    private final ArrayList<MealDBRecipe> apiRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fStore = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listView);
        searchListAdapter = new SearchListAdapter(this, R.layout.list_item_recipe, new ArrayList<>());
        listView.setAdapter(searchListAdapter);

        fetchRecipesFromFirestore();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResultsRecipe clickedRecipe = (ResultsRecipe) parent.getItemAtPosition(position); // Get the clicked recipe
                String recipeId = clickedRecipe.getId(); // Get the ID of the clicked recipe
                Intent intent = new Intent(Search.this, ViewRecipe.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });

    }

    private void fetchRecipesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recipes").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String recipeName = document.getString("Name");
                                String recipeId = document.getId(); // Get recipe document ID
                                List<String> recipeIngredients = (List<String>) document.get("Ingredients");
                                ResultsRecipe resultsRecipe = new ResultsRecipe(recipeName, recipeId, recipeIngredients);
                                // Populate the original list as well
                                originalRecipeList.add(resultsRecipe);
                                filteredRecipeList.add(resultsRecipe);
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
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // If search query is empty, restore the original list
                    filteredRecipeList.clear();
                    filteredRecipeList.addAll(originalRecipeList);
                    updateList();
                } else {
                    // Filter the recipes based on the search query
                    filteredRecipeList.clear();
                    for (ResultsRecipe rec : originalRecipeList) {
                        if (rec.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            filteredRecipeList.add(rec);
                        }
                    }
                    updateList();
                }
                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterRecipes(String filterText) {

    }

    private void updateList() {
        searchListAdapter.clear();
        searchListAdapter.addAll(filteredRecipeList);
    }
}
