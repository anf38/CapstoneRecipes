package com.example.pantrypal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    @Rule
    public ActivityScenarioRule<Login> activityRule = new ActivityScenarioRule<>(Login.class);

    @Rule
    public ActivityScenarioRule<MainActivity> mainActivityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testEmptyFields() {
        // Click on the login button without entering any credentials
        onView(withId(R.id.LoginBtn)).check(matches(isDisplayed())).perform(click());

        try {
            // Add Thread.sleep() to wait for UI elements to load
            Thread.sleep(2000); // Wait for 2 seconds (adjust the time as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testInvalidLogin() {
        // Enter invalid email and password
        onView(withId(R.id.email)).perform(typeText("invalidemail"));
        onView(withId(R.id.password)).perform(typeText("invalidpassword"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.LoginBtn)).check(matches(isDisplayed())).perform(click());
        
        onView(ViewMatchers.withId(R.id.emailPassError)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testValidLogin() {
        // Enter valid email and password
        onView(withId(R.id.email)).perform(typeText("testing@example.com"));
        onView(withId(R.id.password)).perform(typeText("test123"), ViewActions.closeSoftKeyboard());

        // Click on the login button
        onView(withId(R.id.LoginBtn)).check(matches(isDisplayed())).perform(click());

        try {
            // Add Thread.sleep() to wait for UI elements to load
            Thread.sleep(500); // Wait half a second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.logout)).check(matches(isDisplayed())).perform(click());

    }

    @Test
    public void testPasswordVisibility() {
        // Click the "Show Password" checkbox
        onView(withId(R.id.password)).perform(typeText("test123"));

        onView(withId(R.id.passCheckBox)).perform(click());

        onView(withId(R.id.password)).check(matches(withText("test123")));

        // You may also want to check if the checkbox state changes properly
        onView(withId(R.id.passCheckBox)).check(matches(isChecked()));
    }
}