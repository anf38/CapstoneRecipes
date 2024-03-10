package com.example.pantrypal;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import android.text.InputType;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static java.util.EnumSet.allOf;
import static org.hamcrest.Matchers.*;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import com.example.pantrypal.Login;
import com.example.pantrypal.MainActivity;
import com.example.pantrypal.R;

import org.junit.*;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityScenarioRule<Login> activityRule =
            new ActivityScenarioRule<>(Login.class);

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testEmptyFields() {
        // Click on the login button without entering any credentials
        onView(withId(R.id.LoginBtn))
                .check(matches(isDisplayed()))
                .perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.progressBar))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testInvalidLogin() {
        // Enter invalid email and password
        onView(withId(R.id.email))
                .perform(ViewActions.typeText("invalidemail"));
        onView(withId(R.id.password))
                .perform(ViewActions.typeText("invalidpassword"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.LoginBtn))
                .check(matches(isDisplayed()))
                .perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.progressBar))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testValidLogin() {
        // Enter valid email and password
        onView(withId(R.id.email))
                .perform(ViewActions.typeText("testing@example.com"));
        onView(withId(R.id.password))
                .perform(ViewActions.typeText("test123"));

        // Click on the login button
        onView(withId(R.id.LoginBtn))
                .check(matches(isDisplayed()))
                .perform(click());

    }

    @Test
    public void testPasswordVisibility() {
        // Click the "Show Password" checkbox
        onView(withId(R.id.password))
                .perform(ViewActions.typeText("test123"));

        Espresso.onView(ViewMatchers.withId(R.id.passCheckBox))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.password))
                .check(ViewAssertions.matches(ViewMatchers.withText("test123")));

        // You may also want to check if the checkbox state changes properly
        Espresso.onView(ViewMatchers.withId(R.id.passCheckBox))
                .check(ViewAssertions.matches(ViewMatchers.isChecked()));
    }
}