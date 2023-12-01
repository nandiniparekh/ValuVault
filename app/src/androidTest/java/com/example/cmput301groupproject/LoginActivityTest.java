package com.example.cmput301groupproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        // Initialize Intents before each test
        Intents.init();
    }

    @After
    public void tearDown() {
        // Release Intents after each test
        Intents.release();
    }

    @Test
    public void testA_ActivitySwitch() {
        // Type the username "TestUser" in the EditText
        onView(ViewMatchers.withId(R.id.usernameEditText))
                .perform(ViewActions.typeText("TestUser"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Delay for a short time to allow any asynchronous operations to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the MainActivity is started with the correct intent
        Intents.intended(IntentMatchers.hasComponent(MainActivity.class.getName()));
        Intents.intended(IntentMatchers.hasExtraWithKey("userDoc"));
        Intents.intended(IntentMatchers.hasExtra("userDoc", "TestUser"));
    }

    @Test
    public void testB_testEmpty(){
        // Type a spaced username in the EditText
        onView(ViewMatchers.withId(R.id.usernameEditText))
                .perform(ViewActions.typeText("  "), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Check if the LoginActivity is still present
        onView(ViewMatchers.withId(R.id.usernameEditText)).check(matches(isDisplayed()));

        // Type an empty username in the EditText
        onView(ViewMatchers.withId(R.id.usernameEditText))
                .perform(ViewActions.replaceText(""), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Check if the LoginActivity is still present
        onView(ViewMatchers.withId(R.id.usernameEditText)).check(matches(isDisplayed()));
    }
}
