package com.example.pantrypal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RecipeRetriever {
    private final String serverAddress;
    private final int serverPort;

    public RecipeRetriever(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public JSONObject lookUp(int recipeID) {
        return makeRequest("/lookup?id=" + recipeID);
    }

    public JSONObject searchByName(String name) {
        return makeRequest("/search?name=" + name.toLowerCase().replaceAll(" ", "_"));
    }

    public JSONObject searchByFirstChar(char firstChar) {
        return makeRequest("/search?firstchar=" + firstChar);
    }

    public JSONObject randomRecipe() {
        return makeRequest("/random");
    }

    public JSONObject listCategoriesDetailed() {
        return makeRequest("/categories");
    }

    public JSONObject listCategories() {
        return makeRequest("/list?list=categories");
    }

    public JSONObject listAreas() {
        return makeRequest("/list?list=areas");
    }

    public JSONObject listIngredients() {
        return makeRequest("/list?list=ingredients");
    }

    public JSONObject filterByIngredients(List<String> ingredients) {
        StringBuilder ingredientsString = new StringBuilder();

        for (String ingredient : ingredients) {
            ingredientsString.append(ingredient.toLowerCase().replaceAll(" ", "_"))
                    .append(",");
        }
        ingredientsString.deleteCharAt(ingredientsString.length() - 1); // remove last comma

        return makeRequest("/filter?ingredients=" + ingredientsString);
    }

    public JSONObject filterByIngredient(String ingredient)
    {
        return makeRequest("/filter?ingredients=" + ingredient.toLowerCase().replaceAll(" ", "_"));
    }

    public JSONObject filterByCategory(String category) {
        return makeRequest("/filter?category=" + category);
    }

    public JSONObject filterByArea(String area) {
        return makeRequest("/filter?area=" + area);
    }

    // FIXME: This uses unsecure HTTP connection. Some extra steps are necessary to use HTTPS
    private JSONObject makeRequest(String request) {
        JSONObject recipeJSON = null;

        try {
            URL url = new URL("http", serverAddress, serverPort, request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream response = new BufferedInputStream(connection.getInputStream());

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
            e.printStackTrace();
            return null;
        }

        return recipeJSON;
    }
}
