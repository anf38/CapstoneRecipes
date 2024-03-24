import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MealParserTest {
    private MealParser mealParser;
    private JSONObject mealObject;

    @BeforeEach
    public void setUp() {
        mealParser = new MealParser();
        mealObject = new JSONObject();
        mealObject.put("idMeal", "12345");
        mealObject.put("strMeal", "Test Meal");
        mealObject.put("strCategory", "Test Category");
        mealObject.put("strArea", "Test Area");
        mealObject.put("strInstructions", "Test Instructions");
        mealObject.put("strMealThumb", "https://www.example.com/image.jpg");
        mealObject.put("strTags", "Test Tag1, Test Tag2");
        mealObject.put("strYoutube", "https://www.youtube.com/watch?v=test");
        mealObject.put("strIngredient1", "Ingredient 1");
        mealObject.put("strIngredient2", "Ingredient 2");
        mealObject.put("strMeasure1", "1 unit");
        mealObject.put("strMeasure2", "2 units");
    }

    @Test
    public void testParseMeal() {
        Meal meal = mealParser.parseMeal(mealObject);
        assertNotNull(meal);
        assertEquals("12345", meal.getIdMeal());
        assertEquals("Test Meal", meal.getStrMeal());
        assertEquals("Test Category", meal.getStrCategory());
        assertEquals("Test Area", meal.getStrArea());
        assertEquals("Test Instructions", meal.getStrInstructions());
        assertEquals("https://www.example.com/image.jpg", meal.getStrMealThumb());
        assertEquals("Test Tag1, Test Tag2", meal.getStrTags());
        assertEquals("https://www.youtube.com/watch?v=test", meal.getStrYoutube());
        assertEquals("Ingredient 1", meal.getIngredients().get(0));
        assertEquals("Ingredient 2", meal.getIngredients().get(1));
        assertEquals("1 unit", meal.getMeasures().get(0));
        assertEquals("2 units", meal.getMeasures().get(1));
    }

    @Test
    public void testCacheMealAndGetCachedMeal() {
        Meal meal = mealParser.parseMeal(mealObject);
        assertEquals(meal);
    }
}
