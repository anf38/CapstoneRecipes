import org.json.JSONArray;
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

        return meal;
    }


}
