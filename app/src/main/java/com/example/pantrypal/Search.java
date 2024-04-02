package com.example.pantrypal;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class Search extends AppCompatActivity {
    private FirebaseFirestore fStore;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> recipeNames = new ArrayList<>();
    private List<String> recipeIds = new ArrayList<>(); // To store recipe document IDs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fStore = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recipeNames);
        listView.setAdapter(arrayAdapter);

        fetchRecipesFromFirestore();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String recipeName = (String) parent.getItemAtPosition(position); // Get the clicked recipe name
                int index = recipeNames.indexOf(recipeName); // Find the index of the clicked recipe name
                if (index != -1) { // Check if the recipe name exists in the list
                    String recipeId = recipeIds.get(index); // Get the corresponding recipe ID
                    Intent intent = new Intent(Search.this, ViewRecipe.class);
                    intent.putExtra("recipeId", recipeId);
                    startActivity(intent);
                }
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
                                recipeNames.add(recipeName);
                                recipeIds.add(recipeId); // Add recipe document ID to list
                            }
                            arrayAdapter.notifyDataSetChanged();
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
