package com.example.pantrypal;

import android.net.Uri;

public class CreateRecipe {
    private String name;
    private String[] ingredients;
    private String[] instructions;
    private Uri picture;
    private String id;

    CreateRecipe() {
        name = "";
        ingredients = new String[]{};
        instructions = new String[]{};
        id = "";
    }

    public CreateRecipe(String name, String[] ingredients, String[] instructions, String id) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.id = id;
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

    public String id() {
        return id;
    }

    public void setPictureUri(String id) {
        this.id = id;
    }
}
