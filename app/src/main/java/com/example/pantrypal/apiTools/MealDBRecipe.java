package com.example.pantrypal.apiTools;

import com.example.pantrypal.ResultsRecipe;

import java.io.Serializable;
import java.util.List;

public class MealDBRecipe extends ResultsRecipe implements Serializable {
    private final String drinkAlternate;
    private final String category;
    private final String area;
    private final List<String> instructionLines;
    private final List<String> tags;
    private final String youtubeLink;
    String imageURL;
    private final List<String> ingredients;

    public MealDBRecipe(int id,
                        String name,
                        String drinkAlternate,
                        String category,
                        String area,
                        List<String> instructionLines,
                        String imageURL,
                        List<String> tags,
                        String youtubeLink,
                        List<String> ingredients) {
        super(name, String.valueOf(id), ingredients, imageURL);

        this.drinkAlternate = drinkAlternate;
        this.category = category;
        this.area = area;
        this.imageURL = imageURL;
        this.instructionLines = instructionLines;
        this.tags = tags;
        this.youtubeLink = youtubeLink;
        this.ingredients = ingredients;
    }

    public int getIDInt() {
        return Integer.parseInt(super.getId());
    }
    public String getImageURL() {
        return imageURL;
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

    public List<String> getTags() {
        return tags;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public List<String> getIngredients() {
        return ingredients;
    }
}
