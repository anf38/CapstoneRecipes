package com.example.pantrypal;

public class FavoriteRecipe {
    private String Name;
    private String Id;
    private String mealDB;
    private String imageURL;

    public FavoriteRecipe(String Name, String imageURL, String Id, String mealDB) {
        this.Name = Name;
        this.Id = Id;
        this.mealDB = mealDB;
        this.imageURL = imageURL;
    }


    public String getName() {
        return Name;
    }

    public String getId() {
        return Id;
    }

    public String getMealDB() {
        return mealDB;
    }

    public String getimageURL() {
        return imageURL;
    }
}
