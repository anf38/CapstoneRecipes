package com.example.pantrypal.apiTools;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MealDBJSONParser {
    public static List<MealDBRecipe> parseRecipes(JSONObject mealDBJSONObject) {
        List<MealDBRecipe> recipes = new ArrayList<>();

        try {
            JSONArray recipeArray = mealDBJSONObject.getJSONArray("meals");
            int numRecipes = recipeArray.length();

            for (int i = 0; i < numRecipes; ++i) {
                MealDBRecipe recipe = parseRecipe(recipeArray.getJSONObject(i));
                recipes.add(recipe);
            }

        } catch (JSONException e) {
            return null;
        }

        return recipes;
    }

    public static MealDBRecipe parseFirstRecipe(JSONObject mealDBJSONObject) {
        try {
            return parseRecipe(mealDBJSONObject.getJSONArray("meals").getJSONObject(0));
        } catch (JSONException e) {
            return null;
        }
    }

    public static List<String> parseCategories(JSONObject mealDBCategories) {
        List<String> categories = new ArrayList<>();

        try {
            JSONArray categoryArray = mealDBCategories.getJSONArray("meals");
            int numCategories = categoryArray.length();

            for (int i = 0; i < numCategories; ++i) {
                categories.add(categoryArray.getJSONObject(i).getString("strCategory"));
            }
        } catch (JSONException e) {
            return null;
        }

        return categories;
    }

    public static List<MealDBCategory> parseCategoriesDetailed(JSONObject mealDBCategories) {
        List<MealDBCategory> categories = new ArrayList<>();

        try {
            JSONArray categoryArray = mealDBCategories.getJSONArray("categories");
            int numCategories = categoryArray.length();

            for (int i = 0; i < numCategories; ++i) {
                JSONObject category = categoryArray.getJSONObject(i);
                categories.add(new MealDBCategory(category.getInt("idCategory"),
                        category.getString("strCategory"),
                        category.optString("strCategoryThumb"),
                        category.optString("strCategoryDescription")));
            }
        } catch (JSONException e) {
            return null;
        }

        return categories;
    }

    public static List<String> parseAreas(JSONObject mealDBAreas) {
        List<String> areas = new ArrayList<>();

        try {
            JSONArray areaArray = mealDBAreas.getJSONArray("meals");
            int numAreas = areaArray.length();

            for (int i = 0; i < numAreas; ++i) {
                areas.add(areaArray.getJSONObject(i).getString("strArea"));
            }
        } catch (JSONException e) {
            return null;
        }

        return areas;
    }

    public static List<Pair<String, String>> parseIngredients(JSONObject mealDBIngredients) {
        List<Pair<String, String>> ingredients = new ArrayList<>();

        try {
            JSONArray ingredientArray = mealDBIngredients.getJSONArray("meals");
            int numIngredients = ingredientArray.length();

            for (int i = 0; i < numIngredients; ++i) {
                JSONObject ingredient = ingredientArray.getJSONObject(i);
                ingredients.add(new Pair<>(ingredient.getString("strIngredient"), ingredient.optString("strDescription")));
            }
        } catch (JSONException e) {
            return null;
        }

        return ingredients;
    }

    private static MealDBRecipe parseRecipe(JSONObject recipe) {
        List<Map.Entry<String, String>> ingredients = new ArrayList<>();
        for (int i = 1; i <= 20; ++i) {
            String ingredient = recipe.optString("strIngredient" + i);
            String measurement = recipe.optString("strMeasure" + i);
            if (!ingredient.isEmpty() && !ingredient.equals("null")
                    && !measurement.isEmpty() && !measurement.equals("null")) {
                ingredients.add(new AbstractMap.SimpleEntry<>(ingredient, measurement));
            }
        }

        return new MealDBRecipe(recipe.optInt("idMeal"),
                recipe.optString("strMeal"),
                recipe.optString("strDrinkAlternate"),
                recipe.optString("strCategory"),
                recipe.optString("strArea"),
                Arrays.asList(recipe.optString("strInstructions").split("(\r\n)+")),
                recipe.optString("strMealThumb"),
                Arrays.asList(recipe.optString("strTags").split(",")),
                recipe.optString("strYoutube"),
                ingredients);
    }
}
