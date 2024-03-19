package com.example.pantrypal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

    FirebaseFirestore fStore;
    ListView listView;
    ArrayAdapter<String> arrayAdapter;
    List<String> recipeNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fStore = FirebaseFirestore.getInstance();

        listView = findViewById(R.id.listView);
        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, name);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recipeNames);
        listView.setAdapter(arrayAdapter);

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
                                recipeNames.add(recipeName);
                            }
                            arrayAdapter.notifyDataSetChanged(); // Notify adapter after data is fetched
                        } else {
                            // Handle errors
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
