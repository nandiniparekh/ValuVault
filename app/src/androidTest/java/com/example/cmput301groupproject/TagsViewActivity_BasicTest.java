package com.example.cmput301groupproject;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

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
public class TagsViewActivity_BasicTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        // Initialize Intents before each test
        Intents.init();

        // Type the username "TestUser" in the EditText
        onView(ViewMatchers.withId(R.id.usernameEditText))
                .perform(ViewActions.typeText("TestUser_AddTags"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Delay for a short time to allow any asynchronous operations to complete
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the View Tags button
        onView(withId(R.id.tagButton)).perform(click());
    }

    @After
    public void tearDown() {
        // Release Intents after each test
        Intents.release();
    }

    @Test
    public void testA_AddTags() {
        int tagCap = 3;
        for(int i = 1; i <= tagCap; i++) {
            // Click the Add Tags button
            onView(withId(R.id.btnAddTags)).perform(click());

            String tagText = "tag" + i;

            onView(withId(R.id.editTextTag)).perform(ViewActions.typeText(tagText), ViewActions.closeSoftKeyboard());

            // Click on the OK button for adding the item
            onView(withText("OK")).perform(click());
        }

        // Check if the newly added item is displayed in the list
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for(int i = 1; i <= tagCap; i++) {
            String tagText = "tag" + i;
            onView(withText(tagText)).check(matches(isDisplayed()));
        }

    }

    @Test
    public void testB_DeleteTag() {
        // Assuming tagList is not empty, perform an delete action on the first item in the list
        onData(anything())
                .inAdapterView(withId(R.id.define_tag_list))
                .atPosition(0)
                .onChildView(withId(R.id.checkBoxTag))
                .perform(click());



        onView(withId(R.id.btnDeleteTags)).perform(click());
        onView(withText("tag1")).check(doesNotExist());
    }

    @Test
    public void testC_DeleteTags() {
        int tagCap = 2;
        for(int i = 0; i < tagCap; i++) {
            // Assuming dataList is not empty, perform a click on the first item in the list
            onData(anything())
                    .inAdapterView(withId(R.id.define_tag_list))
                    .atPosition(i)
                    .onChildView(withId(R.id.checkBoxTag))
                    .perform(click());
        }

        onView(withId(R.id.btnDeleteTags)).perform(click());

        for(int i = 2; i <= 1 + tagCap; i++) {
            String tagText = "tag" + i;
            onView(withText(tagText)).check(doesNotExist());
        }
    }
}
