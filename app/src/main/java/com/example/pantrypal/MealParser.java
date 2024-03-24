package com.example.pantrypal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Meal {
    private String idMeal;
    private String strMeal;
    private String strCategory;
    private String strArea;
    private String strInstructions;
    private String strMealThumb;
    private String strTags;
    private String strYoutube;
    private List<String> ingredients;
    private List<String> measures;

    // Constructor
    public Meal() {
        ingredients = new ArrayList<>();
        measures = new ArrayList<>();
    }

    // Setter methods
    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }

    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }

    public void setStrCategory(String strCategory) {
        this.strCategory = strCategory;
    }

    public void setStrArea(String strArea) {
        this.strArea = strArea;
    }

    public void setStrInstructions(String strInstructions) {
        this.strInstructions = strInstructions;
    }

    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }

    public void setStrTags(String strTags) {
        this.strTags = strTags;
    }

    public void setStrYoutube(String strYoutube) {
        this.strYoutube = strYoutube;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setMeasures(List<String> measures) {
        this.measures = measures;
    }
    public String getStrYoutube() {
        return strYoutube;
    }

    public String getStrTags() {
        return strTags;
    }

    public String getStrMealThumb() {
        return strMealThumb;
    }

    public String getStrInstructions() {
        return strInstructions;
    }
    public String getStrArea() {
        return strArea;
    }
    public String getStrCategory() {
        return strCategory;
    }
    public String getStrMeal() {
        return strMeal;
    }

    public String getIdMeal() {
        return idMeal;
    }
    public List<String> getIngredients() {
        return ingredients;
    }
    public List<String> getMeasures() {
        return measures;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "idMeal='" + idMeal + '\'' +
                ", strMeal='" + strMeal + '\'' +
                ", strCategory='" + strCategory + '\'' +
                ", strArea='" + strArea + '\'' +
                ", strInstructions='" + strInstructions + '\'' +
                ", strMealThumb='" + strMealThumb + '\'' +
                ", strTags='" + strTags + '\'' +
                ", strYoutube='" + strYoutube + '\'' +
                ", ingredients=" + ingredients +
                ", measures=" + measures +
                '}';
    }
}

class MealParser {
    private Map<String, Meal> mealCache;

    public MealParser() {
        mealCache = new HashMap<>();
    }

    public Meal parseMeal(JSONObject mealObject) {
        Meal meal = new Meal();
        try {
            meal.setIdMeal(mealObject.getString("idMeal"));
            meal.setStrMeal(mealObject.getString("strMeal"));
            meal.setStrCategory(mealObject.getString("strCategory"));
            meal.setStrArea(mealObject.getString("strArea"));
            meal.setStrInstructions(mealObject.getString("strInstructions"));
            meal.setStrMealThumb(mealObject.getString("strMealThumb"));
            meal.setStrTags(mealObject.getString("strTags"));
            meal.setStrYoutube(mealObject.getString("strYoutube"));

            // Parse ingredients and measures
            meal.setIngredients(new ArrayList<>());
            meal.setMeasures(new ArrayList<>());
            for (int i = 1; i <= 20; i++) {
                String ingredient = mealObject.optString("strIngredient" + i);
                String measure = mealObject.optString("strMeasure" + i);
                if (!ingredient.isEmpty() && !measure.isEmpty()) {
                    meal.getIngredients().add(ingredient);
                    meal.getMeasures().add(measure);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return meal;
    }

}
