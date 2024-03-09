package com.example.pantrypal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Test class for the RecipeRetriever.
 * The Spring Boot server is required to be running on the local machine for these tests to pass.
 * Address: 127.0.0.1
 * Port: 8080
 */
public class RecipeRetrieverTest {
    private static final RecipeRetriever recipeRetriever = new RecipeRetriever("127.0.0.1", 8080);

    @Test
    public void getRecipeByID() throws Exception {
        JSONObject teriyakiChickenRecipe = recipeRetriever.lookUp(52772);
        assertTrue(isTeriyakiChickenRecipe(teriyakiChickenRecipe));
    }

    @Test
    public void searchRecipesByName() throws Exception {
        JSONObject teriyakiChickenRecipe = recipeRetriever.searchByName("Teriyaki Chicken Casserole");
        assertTrue(isTeriyakiChickenRecipe(teriyakiChickenRecipe));
    }

    @Test
    public void searchRecipesByFirstChar() throws Exception {
        JSONObject teriyakiChickenRecipe = recipeRetriever.searchByFirstChar('t');
        isTeriyakiChickenRecipe(teriyakiChickenRecipe);
    }

    @Test
    public void getRandomRecipe() throws Exception {
        JSONObject randomRecipe = recipeRetriever.randomRecipe();
        assertTrue(randomRecipe.getJSONArray("meals").getJSONObject(0).has("strInstructions"));
    }

    @Test
    public void getDetailedCategories() throws Exception {
        JSONObject detailedCategories = recipeRetriever.listCategoriesDetailed();
        assertTrue(detailedCategories.getJSONArray("categories").length() >= 13);
        assertTrue(detailedCategories.getJSONArray("categories").getJSONObject(0).has("strCategoryDescription"));
    }

    @Test
    public void listCategories() throws Exception {
        JSONObject categories = recipeRetriever.listCategories();
        assertTrue(categories.getJSONArray("meals").length() >= 13);
        assertFalse(categories.getJSONArray("meals").getJSONObject(0).has("strCategoryDescription"));
        assertEquals("Beef", categories.getJSONArray("meals").getJSONObject(0).getString("strCategory"));
    }

    @Test
    public void listAreas() throws Exception {
        JSONObject areas = recipeRetriever.listAreas();
        assertTrue(areas.getJSONArray("meals").length() >= 27);
        assertTrue(areas.getJSONArray("meals").getJSONObject(0).has("strArea"));
        assertEquals("American", areas.getJSONArray("meals").getJSONObject(0).getString("strArea"));
    }

    @Test
    public void listIngredients() throws Exception {
        JSONObject ingredients = recipeRetriever.listIngredients();
        assertTrue(ingredients.getJSONArray("meals").length() >= 574);
        assertEquals(1, ingredients.getJSONArray("meals").getJSONObject(0).getInt("idIngredient"));
        assertEquals("Chicken", ingredients.getJSONArray("meals").getJSONObject(0).getString("strIngredient"));
    }

    @Test
    public void filterIngredients() throws Exception {
        JSONObject recipes = recipeRetriever.filterByIngredient("Chicken Breasts");
        assertEquals(52772, recipes.getJSONArray("meals").getJSONObject(6).getInt("idMeal"));
        assertEquals("Teriyaki Chicken Casserole", recipes.getJSONArray("meals").getJSONObject(6).getString("strMeal"));
    }

    private boolean isTeriyakiChickenRecipe(JSONObject teriyakiChickenRecipe) throws Exception {
        return 52772 == teriyakiChickenRecipe.getJSONArray("meals").getJSONObject(0).getInt("idMeal")
                && teriyakiChickenRecipe.getJSONArray("meals").getJSONObject(0).getString("strMeal").equals("Teriyaki Chicken Casserole");
    }
}
