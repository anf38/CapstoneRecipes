package com.example.pantrypal.apiTools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecipeRetriever {
    private final String serverAddress;
    private final int serverPort;

    private final ExecutorService asyncExecutor = Executors.newCachedThreadPool();

    public RecipeRetriever(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public JSONObject lookUp(int recipeID) {
        return retrieveJSON("/lookup?id=" + recipeID);
    }

    public JSONObject searchByName(String name) {
        return retrieveJSON("/search?name=" + name.toLowerCase().replaceAll(" ", "_"));
    }

    public JSONObject searchByFirstChar(char firstChar) {
        return retrieveJSON("/search?firstchar=" + firstChar);
    }

    public JSONObject randomRecipe() {
        return retrieveJSON("/random");
    }

    public JSONObject listCategoriesDetailed() {
        return retrieveJSON("/categories");
    }

    public JSONObject listCategories() {
        return retrieveJSON("/list?list=categories");
    }

    public JSONObject listAreas() {
        return retrieveJSON("/list?list=areas");
    }

    public JSONObject listIngredients() {
        return retrieveJSON("/list?list=ingredients");
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

    public JSONObject filterByIngredient(String ingredient) {
        return retrieveJSON("/filter?ingredients=" + ingredient.toLowerCase().replaceAll(" ", "_"));
    }

    public JSONObject filterByCategory(String category) {
        return retrieveJSON("/filter?category=" + category);
    }

    public JSONObject filterByArea(String area) {
        return retrieveJSON("/filter?area=" + area);
    }

    public Bitmap getRecipeImage(int recipeID, boolean small)
    {
        String smallRequest = small ? "&small=true" : "";
        return retrieveImage("/mealimage?id=" + recipeID + smallRequest);
    }

    public Future<Bitmap> getRecipeImageAsync(int recipeID, boolean small) {
        return asyncExecutor.submit(() -> getRecipeImage(recipeID, small));
    }

    // FIXME: This uses unsecure HTTP connection. Some extra steps are necessary to use HTTPS
    private JSONObject retrieveJSON(String request) {
        JSONObject recipeJSON = null;

        try {
            InputStream response = new BufferedInputStream(new URL("http", serverAddress, serverPort, request).openStream());

            int numBytesRead = -1;
            byte[] recipeByteArray = new byte[8192];
            StringBuilder recipeBuilder = new StringBuilder();

            do {
                numBytesRead = response.read(recipeByteArray);
                recipeBuilder.append(new String(recipeByteArray, 0, numBytesRead, StandardCharsets.UTF_8));
            } while (numBytesRead != -1 && response.available() > 0);

            recipeJSON = new JSONObject(recipeBuilder.toString());

            response.close();
        } catch (IOException | JSONException e) {
            Log.e("Retrieve JSON", e.getMessage());
        }

        return recipeJSON;
    }

    private Bitmap retrieveImage(String imageRequest) {
        Bitmap image = null;

        try {
            InputStream imageStream = new URL("http", serverAddress, serverPort, imageRequest).openStream();
            image = BitmapFactory.decodeStream(imageStream);
        } catch (IOException e) {
            Log.e("Retrieve Image", e.getMessage());
            e.printStackTrace();
        }

        return image;
    }
}
