package com.example.pantrypal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.pantrypal.apiTools.RecipeRetriever;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Test class for the RecipeRetriever.
 * The server is required to be running for these tests to pass.
 * You must also set the envisonment variable "ID_TOKEN" to the value of your Firebase ID token
 * In Linux: export ID_TOKEN=[yourIdToken]
 */
public class RecipeRetrieverTest {
    private static final RecipeRetriever recipeRetriever =
            RecipeRetriever.getInstance(System.getProperty("ID_TOKEN"));

    @AfterClass
    public static void shutdown() {
        recipeRetriever.shutdown();
    }

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
        JSONObject randomRecipe = recipeRetriever.randomRecipe(false);
        assertTrue(randomRecipe.getJSONArray("meals").getJSONObject(0).has("strInstructions"));
    }

    @Test
    public void getTenRandomRecipes() throws Exception {
        JSONObject randomRecipes = recipeRetriever.randomRecipe(true);
        assertEquals(10, randomRecipes.getJSONArray("meals").length());
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
        assertTrue(recipes.has("meals"));
    }

    private boolean isTeriyakiChickenRecipe(JSONObject teriyakiChickenRecipe) throws Exception {
        return 52772 == teriyakiChickenRecipe.getJSONArray("meals").getJSONObject(0).getInt("idMeal")
                && teriyakiChickenRecipe.getJSONArray("meals").getJSONObject(0).getString("strMeal").equals("Teriyaki Chicken Casserole");
    }
}
