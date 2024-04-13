package com.example.pantrypal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultsRecipe implements Serializable {
    protected String title;
    protected String id;
    protected List<String> ingredients;

    public ResultsRecipe(String title, String id, List<String> ingredients) {
        this.title = title;
        this.id = id;
        this.ingredients = new ArrayList<>(ingredients);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
