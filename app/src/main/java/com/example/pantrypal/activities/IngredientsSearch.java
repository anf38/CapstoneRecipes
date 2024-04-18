package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

public class IngredientsSearch extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private final RecipeRetriever recipeRetriever = new RecipeRetriever();

    private ListView ingredientsListView, resultsListView;
    private ArrayAdapter<String> ingredientsArrayAdapter;
    private final List<String> ingredients = new ArrayList<>();
    private SearchListAdapter searchListAdapter;

    private EditText etIngredientInput;
    private Button btnAddIngredient;
    private ImageButton expandButton;

    private final ArrayList<ResultsRecipe> communityRecipes = new ArrayList<>();
    private final ArrayList<ResultsRecipe> filteredRecipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientssearch);
        fStore = FirebaseFirestore.getInstance();
        etIngredientInput = findViewById(R.id.etIngredientInput);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        expandButton = findViewById(R.id.expandButton);

        ingredientsListView = findViewById(R.id.ingredientsListView);
        resultsListView = findViewById(R.id.resultsListView);
        ingredientsArrayAdapter = new ArrayAdapter<>(this, R.layout.ingredient_list_row, R.id.textViewIngredient, ingredients);
        searchListAdapter = new SearchListAdapter(this, R.layout.list_item_recipe, filteredRecipes);
        ingredientsListView.setAdapter(ingredientsArrayAdapter);
        resultsListView.setAdapter(searchListAdapter);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of ingredientsListView
                if (ingredientsListView.getVisibility() == View.VISIBLE) {
                    ingredientsListView.setVisibility(View.GONE);
                    expandButton.setImageResource(R.drawable.expand_more);
                } else {
                    ingredientsListView.setVisibility(View.VISIBLE);
                    expandButton.setImageResource(R.drawable.expand_less);
                }
            }
        });

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = etIngredientInput.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient)) {
                    if (ingredients.size() < 7) {
                        resultsListView.setVisibility(View.VISIBLE);
                        ingredients.add(ingredient);
                        ingredientsArrayAdapter.notifyDataSetChanged();
                        etIngredientInput.setText("");
                        filterRecipes();
                    } else {
                        Toast.makeText(IngredientsSearch.this, "Maximum ingredients reached", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) instanceof MealDBRecipe) {
                    MealDBRecipe apiRecipe = (MealDBRecipe) parent.getItemAtPosition(position);
                    Intent intent = new Intent(IngredientsSearch.this, ViewMealDBRecipe.class);
                    intent.putExtra("recipe", apiRecipe);
                    startActivity(intent);
                } else {
                    ResultsRecipe clickedRecipe = (ResultsRecipe) parent.getItemAtPosition(position); // Get the clicked recipe
                    String recipeId = clickedRecipe.getId(); // Get the ID of the clicked recipe

                    Intent intent = new Intent(IngredientsSearch.this, ViewRecipe.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                }
            }
        });

        fetchRecipesFromFirestore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recipeRetriever.shutdown();
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
                                String recipeId = document.getId();
                                List<String> recipeIngredients = (List<String>) document.get("Ingredients");
                                ResultsRecipe resultsRecipe = new ResultsRecipe(recipeName, recipeId, recipeIngredients);

                                communityRecipes.add(resultsRecipe);
                                filteredRecipes.add(resultsRecipe);
                            }
                            searchListAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    // Method to handle delete ImageView click event
    public void onDeleteClicked(View view) {
        int position = ingredientsListView.getPositionForView((View) view.getParent());

        if (position != ListView.INVALID_POSITION) {
            ingredients.remove(position);
            ingredientsArrayAdapter.notifyDataSetChanged();
            filterRecipes();
        }
    }

    private void filterRecipes() {
        filteredRecipes.clear();

        // Get API recipes
        if (!ingredients.isEmpty()) {
            new Thread(() -> {
                List<MealDBRecipe> recipeIds =
                        MealDBJSONParser.parseRecipes(recipeRetriever.filterByIngredients(ingredients));

                if (recipeIds != null) {
                    recipeIds.parallelStream().forEach(id ->
                            filteredRecipes.add(MealDBJSONParser.parseFirstRecipe(recipeRetriever.lookUp(id.getIDInt()))));

                    runOnUiThread(() -> searchListAdapter.notifyDataSetChanged());
                }
            }, "SearchByIngredients").start();
        } else {
            resultsListView.setVisibility(View.GONE);
        }

        // Filter community recipes
        for (ResultsRecipe recipe : communityRecipes) {
            boolean containsAllIngredients = true;

            for (String ingredient : ingredients) {
                boolean containsIngredient = false;
                for (String recipeIngredient : recipe.getIngredients()) {
                    if (recipeIngredient.toLowerCase().contains(ingredient.toLowerCase())) {
                        containsIngredient = true;
                        break; // No need to check other ingredients if one match is found
                    }
                }
                if (!containsIngredient) {
                    containsAllIngredients = false;
                    break; // No need to check other ingredients if one is missing
                }
            }

            if (containsAllIngredients) {
                filteredRecipes.add(recipe);
            }
        }
        searchListAdapter.notifyDataSetChanged();
    }

}