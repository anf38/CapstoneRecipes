package com.example.pantrypal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class IngredientsSearch extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private ListView ingredientsListView, resultsListView;
    private ArrayAdapter<String> ingredientsArrayAdapter, resultsArrayAdapter;
    private List<String> ingredients = new ArrayList<>();
    private List<String> recipeNames = new ArrayList<>();
    private List<String> filteredNames = new ArrayList<>();
    private EditText etIngredientInput;
    private Button btnAddIngredient;

    private ImageButton expandButton;

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
        resultsArrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_recipe, R.id.textViewRecipeName, filteredNames);
        ingredientsListView.setAdapter(ingredientsArrayAdapter);
        resultsListView.setAdapter(resultsArrayAdapter);

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

        fetchRecipesFromFirestore();
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
                                List<String> recipeIngredients = (List<String>) document.get("Ingredients");

                                if (recipeName != null && recipeIngredients != null) {
                                    StringBuilder recipeInfo = new StringBuilder();
                                    recipeInfo.append(recipeName).append(": ");
                                    for (String ingredient : recipeIngredients) {
                                        recipeInfo.append(ingredient).append(", ");
                                    }
                                    recipeNames.add(recipeInfo.toString());
                                    filteredNames.add(recipeName);
                                }
                            }
                            resultsArrayAdapter.notifyDataSetChanged();
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
        List<String> tempNames = new ArrayList<>();
        Boolean containsIngredient;
        tempNames = recipeNames;
        filteredNames.clear();

        for (int i = 0; i < recipeNames.size(); i++) {//for every recipe
            containsIngredient = true;
            for (String ingredient : ingredients) {
                if (!recipeNames.get(i).contains(ingredient))
                    containsIngredient = false;
            }
            if (containsIngredient)
                filteredNames.add(tempNames.get(i));
        }

        for (int i = 0; i < filteredNames.size(); i++) {
            String filtered = filteredNames.get(i);
            int indexOfColon = filtered.indexOf(":");
            if (indexOfColon != -1) { //if ":" is found
                filtered = filtered.substring(0, indexOfColon).trim();
            }
            filteredNames.set(i, filtered);
        }
        resultsArrayAdapter.notifyDataSetChanged();
    }
}