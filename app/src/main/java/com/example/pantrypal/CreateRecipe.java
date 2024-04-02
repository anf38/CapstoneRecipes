package com.example.pantrypal;

import android.net.Uri;

public class CreateRecipe {
    private String name;
    private String[] ingredients;
    private String[] instructions;
    private Uri picture;

    CreateRecipe() {
        name = "";
        ingredients = new String[]{};
        instructions = new String[]{};
    }

    public CreateRecipe(String name, String[] ingredients, String[] instructions) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String[] getInstructions() {
        return instructions;
    }

    public void setInstructions(String[] instructions) {
        this.instructions = instructions;
    }

    public Uri getPictureUri() {
        return picture;
    }

    public void setPictureUri(Uri picture) {
        this.picture = picture;
    }
}
