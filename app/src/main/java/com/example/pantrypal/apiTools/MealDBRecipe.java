package com.example.pantrypal.apiTools;

import android.util.Pair;

import java.util.List;

public class MealDBRecipe {
    private final int id;
    private final String name;
    private final String drinkAlternate;
    private final String category;
    private final String area;
    private final List<String> instructionLines;
    private final String imageURL;
    private final List<String> tags;
    private final String youtubeLink;
    private final List<Pair<String, String>> ingredients;

    public MealDBRecipe(int id,
                        String name,
                        String drinkAlternate,
                        String category,
                        String area,
                        List<String> instructionLines,
                        String imageURL,
                        List<String> tags,
                        String youtubeLink,
                        List<Pair<String, String>> ingredients)
    {
        this.id = id;
        this.name = name;
        this.drinkAlternate = drinkAlternate;
        this.category = category;
        this.area = area;
        this.instructionLines = instructionLines;
        this.imageURL = imageURL;
        this.tags = tags;
        this.youtubeLink = youtubeLink;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDrinkAlternate() {
        return drinkAlternate;
    }

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public List<String> getInstructionLines() {
        return instructionLines;
    }

    public String getImageURL() {
        return imageURL;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public List<Pair<String, String>> getIngredients() {
        return ingredients;
    }
}
