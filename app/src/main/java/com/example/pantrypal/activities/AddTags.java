package com.example.pantrypal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pantrypal.R;

import java.util.ArrayList;
import java.util.Arrays;

public class AddTags extends AppCompatActivity {

    private ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<String> checkedTags = new ArrayList<>();

    private String name = "", ingredients = "", instructions = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tags_page);

        name = getIntent().getStringExtra("name");
        instructions = Arrays.toString(getIntent().getStringArrayExtra("instructions"));
        ingredients = Arrays.toString(getIntent().getStringArrayExtra("ingredients"));


        Button cancelRecipeButton = findViewById(R.id.cancelRecipeButton);
        Button createRecipeButton = findViewById(R.id.createRecipeButton);

        // Initializing CheckBoxes
        CheckBox beginnerTag = findViewById(R.id.beginnerTag);
        CheckBox easyTag = findViewById(R.id.easyTag);
        CheckBox moderateTag = findViewById(R.id.moderateTag);
        CheckBox advancedTag = findViewById(R.id.AdvancedTag);
        CheckBox hardTag = findViewById(R.id.hardTag);
        CheckBox expertTag = findViewById(R.id.expertTag);

        CheckBox sweetTag = findViewById(R.id.sweetTag);
        CheckBox sourTag = findViewById(R.id.sourTag);
        CheckBox savoryTag = findViewById(R.id.savoryTag);
        CheckBox saltyTag = findViewById(R.id.saltyTag);
        CheckBox bitterTag = findViewById(R.id.bitterTag);
        CheckBox umamiTag = findViewById(R.id.umamiTag);

        CheckBox drinkTag = findViewById(R.id.drinkTag);
        CheckBox snackTag = findViewById(R.id.snackTag);
        CheckBox breakfastTag = findViewById(R.id.breakfastTag);
        CheckBox lunchTag = findViewById(R.id.lunchTag);
        CheckBox dinnerTag = findViewById(R.id.dinnerTag);
        CheckBox dessertTag = findViewById(R.id.dessertTag);

        CheckBox beefTag = findViewById(R.id.beefTag);
        CheckBox porkTag = findViewById(R.id.porkTag);
        CheckBox chickenTag = findViewById(R.id.chickenTag);
        CheckBox turkeyTag = findViewById(R.id.turkeyTag);
        CheckBox duckTag = findViewById(R.id.duckTag);
        CheckBox poultryTag = findViewById(R.id.poultryTag);
        CheckBox fishTag = findViewById(R.id.fishTag);
        CheckBox lambTag = findViewById(R.id.lambTag);
        CheckBox rabbitTag = findViewById(R.id.rabbitTag);
        CheckBox deerTag = findViewById(R.id.deerTag);
        CheckBox vegetarianTag = findViewById(R.id.vegetarianTag);
        CheckBox veganTag = findViewById(R.id.veganTag);

        CheckBox nutsTag = findViewById(R.id.nutsTag);
        CheckBox dairyTag = findViewById(R.id.dairyTag);
        CheckBox eggsTag = findViewById(R.id.eggsTag);
        CheckBox glutenTag = findViewById(R.id.glutenTag);
        CheckBox shellfishTag = findViewById(R.id.shellfishTag);
        CheckBox sesameTag = findViewById(R.id.sesameTag);

        checkBoxes.add(beginnerTag);
        checkBoxes.add(easyTag);
        checkBoxes.add(moderateTag);
        checkBoxes.add(advancedTag);
        checkBoxes.add(hardTag);
        checkBoxes.add(expertTag);
        checkBoxes.add(sweetTag);
        checkBoxes.add(sourTag);
        checkBoxes.add(savoryTag);
        checkBoxes.add(saltyTag);
        checkBoxes.add(bitterTag);
        checkBoxes.add(umamiTag);
        checkBoxes.add(drinkTag);
        checkBoxes.add(snackTag);
        checkBoxes.add(breakfastTag);
        checkBoxes.add(lunchTag);
        checkBoxes.add(dinnerTag);
        checkBoxes.add(dessertTag);
        checkBoxes.add(beefTag);
        checkBoxes.add(porkTag);
        checkBoxes.add(chickenTag);
        checkBoxes.add(turkeyTag);
        checkBoxes.add(duckTag);
        checkBoxes.add(poultryTag);
        checkBoxes.add(fishTag);
        checkBoxes.add(lambTag);
        checkBoxes.add(rabbitTag);
        checkBoxes.add(deerTag);
        checkBoxes.add(vegetarianTag);
        checkBoxes.add(veganTag);
        checkBoxes.add(nutsTag);
        checkBoxes.add(dairyTag);
        checkBoxes.add(eggsTag);
        checkBoxes.add(glutenTag);
        checkBoxes.add(shellfishTag);
        checkBoxes.add(sesameTag);


        createRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (CheckBox checkBox : checkBoxes) {
                    onCheckBoxClicked(checkBox);
                }


                Intent intent = new Intent(AddTags.this, NewRecipe.class);
                intent.putExtra("name", name);
                intent.putExtra("instructions", getIntent().getStringArrayExtra("instructions"));
                intent.putExtra("ingredients", getIntent().getStringArrayExtra("ingredients"));
                intent.putStringArrayListExtra("tags", checkedTags);
                startActivity(intent);
                finish();
            }
        });
    }

    private void onCheckBoxClicked(CheckBox checkBox) {
        // Check if the checkbox is checked
        if (checkBox.isChecked()) {
            // Add the text of the checked checkbox to the array
            checkedTags.add(checkBox.getText().toString());
        } else {
            // If unchecked, remove it from the array
            checkedTags.remove(checkBox.getText().toString());
        }
    }
}
