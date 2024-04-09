package com.example.pantrypal;

import java.util.ArrayList;
import java.util.List;

public class ResultsRecipe {
    private String title;
    private String id;
    private List<String> ingredients;

    ResultsRecipe() {
        this.title = "Title";
        this.id = "id String";
        this.ingredients = new ArrayList<>();
    }

    ResultsRecipe(String title, String id) {
        this.title = title;
        this.id = id;
    }

    ResultsRecipe(String title, String id, List<String> ingredients) {
        this.title = title;
        this.id = id;
        this.ingredients = new ArrayList<>(ingredients);
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    void setId(String id) {
        this.id = id;
    }

    String getId() {
        return id;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
