package com.example.cmput301groupproject;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ListActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private String cityName = "TestCity";

    @Before
    public void setup() {
        // Initialize Intents
        Intents.init();

        // Click the add item button
        onView(withId(R.id.add_item_b)).perform(click());
        // Type in the required fields for the new item
        onView(withId(R.id.purchase_date_edit_text)).perform(ViewActions.typeText("2023-11-09"));
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("Test description"));
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("Test make"));
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("Test model"));
        onView(withId(R.id.serial_number_edit_text)).perform(ViewActions.typeText("Test serial number"));
        onView(withId(R.id.estimated_value_edit_text)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.comment_edit_text)).perform(ViewActions.typeText("Test comment"));
        // Click on the OK button for adding the item
        onView(withText("OK")).perform(click());
    }

    @After
    public void tearDown() {
        // Release Intents after the test
        Intents.release();
        // Assuming dataList is not empty, perform a click on the first item in the list
        onView(withId(R.id.item_list)).perform(click());
        // Click on the delete option
        onView(withText("Remove")).perform(click());
    }

    @Test
    public void testActivitySwitch() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Check if the ShowActivity is launched
        intended(hasComponent(ListActivity.class.getName()));
    }

    @Test
    public void testCityNameConsistency() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Check if the description name in ListActivity matches the expected city name
//        onView(withId(R.id.city_name_textView))
//                .check(matches(withText(cityName)));
    }

    @Test
    public void testBackButton() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Wait for the view to load
        onView(ViewMatchers.withId(R.id.select_item_list)).check(matches(isDisplayed()));

        // Simulate pressing the back button
        onView(withId(R.id.backSelectButton)).perform(click());

        // Check if the MainActivity is displayed after pressing the back button
        onView(ViewMatchers.withId(R.id.item_list)).check(matches(isDisplayed()));
    }
}
