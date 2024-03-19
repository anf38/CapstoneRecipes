package com.example.pantrypal.apiTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class RecipeRetriever {
    private final String serverAddress;

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public RecipeRetriever(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public JSONObject lookUp(int recipeID) {
        return retrieveJSON("/lookup?id=" + recipeID);
    }

    public Future<JSONObject> lookUpAsync(int recipeID) {
        return asyncExecutor.submit(() -> lookUp(recipeID));
    }

    public JSONObject searchByName(String name) {
        return retrieveJSON("/search?name=" + name.toLowerCase().replaceAll(" ", "_"));
    }

    public Future<JSONObject> searchByNameAsync(String name) {
        return asyncExecutor.submit(() -> searchByName(name));
    }

    public JSONObject searchByFirstChar(char firstChar) {
        return retrieveJSON("/search?firstchar=" + firstChar);
    }

    public Future<JSONObject> searchByFirstCharAsync(char firstChar) {
        return asyncExecutor.submit(() -> searchByFirstChar(firstChar));
    }

    public JSONObject randomRecipe(boolean multiple) {
        String multipleStr = multiple ? "?multiple=true" : "";

        return retrieveJSON("/random" + multipleStr);
    }

    public Future<JSONObject> randomRecipeAsync(boolean multiple) {
        return asyncExecutor.submit(() -> randomRecipe(multiple));
    }

    public JSONObject latestRecipes() {
        return retrieveJSON("/latest");
    }

    public Future<JSONObject> latestRecipesAsync() {
        return asyncExecutor.submit(this::latestRecipes);
    }

    public JSONObject listCategoriesDetailed() {
        return retrieveJSON("/categories");
    }

    public Future<JSONObject> listCategoriesDetailedAsync()
    {
        return asyncExecutor.submit(this::listCategoriesDetailed);
    }

    public JSONObject listCategories() {
        return retrieveJSON("/list?list=categories");
    }

    public Future<JSONObject> listCategoriesAsync() {
        return asyncExecutor.submit(this::listCategories);
    }

    public JSONObject listAreas() {
        return retrieveJSON("/list?list=areas");
    }

    public Future<JSONObject> listAreasAsync()
    {
        return asyncExecutor.submit(this::listAreas);
    }

    public JSONObject listIngredients() {
        return retrieveJSON("/list?list=ingredients");
    }

    public Future<JSONObject> listIngredientsAsync() {
        return asyncExecutor.submit(this::listIngredients);
    }

    public JSONObject filterByIngredients(List<String> ingredients) {
        StringBuilder ingredientsString = new StringBuilder();

        for (String ingredient : ingredients) {
            ingredientsString.append(ingredient.toLowerCase().replaceAll(" ", "_"))
                    .append(",");
        }
        ingredientsString.deleteCharAt(ingredientsString.length() - 1); // remove last comma

        return retrieveJSON("/filter?ingredients=" + ingredientsString);
    }

    public Future<JSONObject> filterByIngredientsAsync(List<String> ingredients) {
        return asyncExecutor.submit(() -> filterByIngredients(ingredients));
    }

    public JSONObject filterByIngredient(String ingredient) {
        return retrieveJSON("/filter?ingredients=" + ingredient.toLowerCase().replaceAll(" ", "_"));
    }

    public Future<JSONObject> filterByIngredientAsync(String ingredient) {
        return asyncExecutor.submit(() -> filterByIngredient(ingredient));
    }

    public JSONObject filterByCategory(String category) {
        return retrieveJSON("/filter?category=" + category);
    }

    public Future<JSONObject> filterByCategoryAsync(String category) {
        return asyncExecutor.submit(() -> filterByCategory(category));
    }

    public JSONObject filterByArea(String area) {
        return retrieveJSON("/filter?area=" + area);
    }

    public Future<JSONObject> filterByAreaAsync(String area) {
        return asyncExecutor.submit(() -> filterByArea(area));
    }

    public Bitmap getRecipeImage(int recipeID, boolean small) {
        String smallRequest = small ? "&small=true" : "";
        return retrieveImage("/mealimage?id=" + recipeID + smallRequest);
    }

    public Future<Bitmap> getRecipeImageAsync(int recipeID, boolean small) {
        return asyncExecutor.submit(() -> getRecipeImage(recipeID, small));
    }

    private JSONObject retrieveJSON(String request) {
        JSONObject recipeJSON = null;

        try {
            URL url = new URL("https", serverAddress, request);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            InputStream response = new BufferedInputStream(connection.getInputStream());

            long contentLenght = Integer.parseInt(connection.getHeaderField("Content-length"));

            int numBytesRead = 0;
            long totalBytesRead = 0L;
            byte[] recipeByteArray = new byte[8192];
            StringBuilder recipeBuilder = new StringBuilder();

            do {
                numBytesRead = response.read(recipeByteArray);
                totalBytesRead += numBytesRead;
                recipeBuilder.append(new String(recipeByteArray, 0, numBytesRead, StandardCharsets.UTF_8));
            } while (totalBytesRead < contentLenght);

            recipeJSON = new JSONObject(recipeBuilder.toString());

            response.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }

        return recipeJSON;
    }

    private Bitmap retrieveImage(String imageRequest) {
        Bitmap image = null;

        try {
            URL url = new URL("https", serverAddress, imageRequest);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            InputStream imageStream = new BufferedInputStream(connection.getInputStream());

            image = BitmapFactory.decodeStream(imageStream);
        } catch (IOException e) {
            Log.e("Retrieve Image", e.getMessage());
            e.printStackTrace();
        }

        return image;
    }
}
