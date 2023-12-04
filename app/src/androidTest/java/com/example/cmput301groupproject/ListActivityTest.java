package com.example.cmput301groupproject;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
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
public class ListActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        // Initialize Intents before each test
        Intents.init();

        // Type the username "TestUser" in the EditText
        onView(ViewMatchers.withId(R.id.usernameEditText))
                .perform(ViewActions.typeText("TestUser_Tags"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Delay for a short time to allow any asynchronous operations to complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        // Release Intents after the test
        Intents.release();
    }

    @Test
    public void testA_ActivitySwitch() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Check if the ListActivity is launched
        intended(hasComponent(ListActivity.class.getName()));

        onView(withId(R.id.backSelectButton)).perform(click());

        // Check if the MainActivity is launched
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testB_ItemDescriptionConsistency() {
        // Click the add item button
        onView(withId(R.id.add_item_b)).perform(click());
        // Type in the required fields for the new item
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("Test description"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("Test make"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("Test model"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.serial_number_edit_text)).perform(ViewActions.typeText("Test serial number"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.estimated_value_edit_text)).perform(ViewActions.typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.comment_edit_text)).perform(ViewActions.typeText("Test comment"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.purchase_date_edit_text)).perform(ViewActions.typeText("2023/11/09"), ViewActions.closeSoftKeyboard());
        // Click on the OK button for adding the item
        onView(withId(R.id.ok_button)).perform(click());

        // Click the add item button
        onView(withId(R.id.add_item_b)).perform(click());
        // Type in the required fields for the new item
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("Test description2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("Test make2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("Test model2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.serial_number_edit_text)).perform(ViewActions.typeText("Test serial number2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.estimated_value_edit_text)).perform(ViewActions.typeText("1000"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.comment_edit_text)).perform(ViewActions.typeText("Test comment2"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.purchase_date_edit_text)).perform(ViewActions.typeText("2023/11/08"), ViewActions.closeSoftKeyboard());

        // Click on the OK button for adding the item
        onView(withId(R.id.ok_button)).perform(click());

        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Check if the description name in ListActivity matches the expected household name
        onView(withText("Test description")).check(matches(isDisplayed()));
        onView(withText("Test description2")).check(matches(isDisplayed()));
    }

    @Test
    public void testC_ApplyTags() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Wait for the view to load
        onView(ViewMatchers.withId(R.id.select_item_list)).check(matches(isDisplayed()));

        for(int i = 0; i < 2; i++) {
            // Assuming dataList is not empty, perform a click on the checkboxes in the list
            onData(anything())
                    .inAdapterView(withId(R.id.select_item_list))
                    .atPosition(i)
                    .onChildView(withId(R.id.checkbox))
                    .perform(click());
        }

        // Simulate pressing the apply tags button
        onView(withId(R.id.applyTagsButton)).perform(click());

        // Assuming tagsList is not empty, apply the first tag in the list to both items
        onData(anything())
                .inAdapterView(withId(R.id.tag_list))
                .atPosition(0)
                .onChildView(withId(R.id.checkBoxTag))
                .perform(click());
        onView(withId(R.id.apply_tags_button)).perform(click());

        // Verify tag application
        for(int i = 0; i < 2; i++) {
            onData(anything())
                    .inAdapterView(withId(R.id.item_list))
                    .atPosition(i)
                    .perform(click());
            onView(withText("tag1")).check(matches(isDisplayed()));
            onView(withId(R.id.cancel_button)).perform(click());
        }
    }

    @Test
    public void testD_CancelButton() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Wait for the view to load
        onView(ViewMatchers.withId(R.id.select_item_list)).check(matches(isDisplayed()));

        for(int i = 0; i < 2; i++) {
            // Assuming dataList is not empty, perform a click on the checkboxes in the list
            onData(anything())
                    .inAdapterView(withId(R.id.select_item_list))
                    .atPosition(i)
                    .onChildView(withId(R.id.checkbox))
                    .perform(click());
        }

        // Simulate pressing the apply tags button
        onView(withId(R.id.applyTagsButton)).perform(click());

        // Assuming tagsList is not empty, start to apply the first tag in the list to both items and then cancel
        onData(anything())
                .inAdapterView(withId(R.id.tag_list))
                .atPosition(0)
                .onChildView(withId(R.id.checkBoxTag))
                .perform(click());
        onView(withId(R.id.cancel_button)).perform(click());

        // Check if the ListActivity is still there
        intended(hasComponent(ListActivity.class.getName()));

    }

    @Test
    public void testZ_DeleteButton() {
        // Click the select items button to launch the ListActivity
        onView(withId(R.id.selectButton)).perform(click());

        // Wait for the view to load
        onView(ViewMatchers.withId(R.id.select_item_list)).check(matches(isDisplayed()));

        for(int i = 0; i < 2; i++) {
            // Assuming dataList is not empty, perform a click on the checkboxes in the list
            onData(anything())
                    .inAdapterView(withId(R.id.select_item_list))
                    .atPosition(i)
                    .onChildView(withId(R.id.checkbox))
                    .perform(click());
        }
        // Simulate pressing the delete button
        onView(withId(R.id.deleteSelectedItemsButton)).perform(click());

        // Check if the item is displayed after pressing the back button
        onView(withText("Test description")).check(doesNotExist());
        onView(withText("Test description2")).check(doesNotExist());
    }
}
