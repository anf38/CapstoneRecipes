package com.example.pantrypal.apiTools;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class MealDBRecipe implements Serializable {
    private final int id;
    private final String name;
    private final String drinkAlternate;
    private final String category;
    private final String area;
    private final List<String> instructionLines;
    private final String imageURL;
    private final List<String> tags;
    private final String youtubeLink;
    private final List<Map.Entry<String, String>> ingredients;

    public MealDBRecipe(int id,
                        String name,
                        String drinkAlternate,
                        String category,
                        String area,
                        List<String> instructionLines,
                        String imageURL,
                        List<String> tags,
                        String youtubeLink,
                        List<Map.Entry<String, String>> ingredients) {
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

    public List<Map.Entry<String, String>> getIngredients() {
        return ingredients;
    }
}
