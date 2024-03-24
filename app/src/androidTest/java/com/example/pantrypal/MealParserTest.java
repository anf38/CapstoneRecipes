package com.example.pantrypal;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MealParserTest {

    @Test
    public void testParseMeal() {
        JSONObject mealObject = new JSONObject();
        try {
            mealObject.put("idMeal", "1");
            mealObject.put("strMeal", "Test Meal");
            mealObject.put("strCategory", "Test Category");
            mealObject.put("strArea", "Test Area");
            mealObject.put("strInstructions", "Test Instructions");
            mealObject.put("strMealThumb", "test_thumbnail.jpg");
            mealObject.put("strTags", "Test Tags");
            mealObject.put("strYoutube", "https://www.youtube.com/test");
            mealObject.put("strIngredient1", "Ingredient 1");
            mealObject.put("strMeasure1", "Measure 1");

            MealParser mealParser = new MealParser();
            Meal meal = mealParser.parseMeal(mealObject);

            assertNotNull(meal);
            assertEquals("1", meal.getIdMeal());
            assertEquals("Test Meal", meal.getStrMeal());
            assertEquals("Test Category", meal.getStrCategory());
            assertEquals("Test Area", meal.getStrArea());
            assertEquals("Test Instructions", meal.getStrInstructions());
            assertEquals("test_thumbnail.jpg", meal.getStrMealThumb());
            assertEquals("Test Tags", meal.getStrTags());
            assertEquals("https://www.youtube.com/test", meal.getStrYoutube());

            List<String> ingredients = meal.getIngredients();
            assertNotNull(ingredients);
            assertEquals(1, ingredients.size());
            assertEquals("Ingredient 1", ingredients.get(0));

            List<String> measures = meal.getMeasures();
            assertNotNull(measures);
            assertEquals(1, measures.size());
            assertEquals("Measure 1", measures.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
