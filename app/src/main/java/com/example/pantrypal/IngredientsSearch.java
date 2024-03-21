package com.example.pantrypal;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

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
    FirebaseFirestore fStore;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> recipeNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredientssearch);
        fStore = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeNames);
        listView.setAdapter(arrayAdapter);

        EditText etIngredientInput = findViewById(R.id.etIngredientInput);
        Button btnAddIngredient = findViewById(R.id.btnAddIngredient);

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredient = etIngredientInput.getText().toString().trim();
                if (!TextUtils.isEmpty(ingredient)) {
                    recipeNames.add(ingredient);
                    arrayAdapter.notifyDataSetChanged();
                    etIngredientInput.setText(""); // Clear EditText after adding ingredient
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
                                } else {
                                    // Handle the case where Name or Ingredients field is missing
                                    recipeNames.add("Recipe details incomplete");
                                }
                            }
                            arrayAdapter.notifyDataSetChanged(); // Notify adapter after data is fetched
                        } else {
                            // Handle task failure here
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
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
