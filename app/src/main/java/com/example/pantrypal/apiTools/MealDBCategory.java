package com.example.pantrypal.apiTools;

public class MealDBCategory {
    private final int id;
    private final String name;
    private final String imageURL;
    private final String description;

    public MealDBCategory(int id,
                          String name,
                          String imageURL,
                          String description) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getDescription() {
        return description;
    }
}
