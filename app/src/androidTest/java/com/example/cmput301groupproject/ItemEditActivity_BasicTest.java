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
public class ItemEditActivity_BasicTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        // Initialize Intents before each test
        Intents.init();

        // Type the username "TestUser" in the EditText
        onView(ViewMatchers.withId(R.id.usernameEditText))
                .perform(ViewActions.typeText("TestUser"), ViewActions.closeSoftKeyboard());

        // Click the login button
        onView(ViewMatchers.withId(R.id.loginButton))
                .perform(ViewActions.click());

        // Delay for a short time to allow any asynchronous operations to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        // Release Intents after each test
        Intents.release();
    }

    @Test
    public void testA_AddItem() {
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
        // Check if the newly added item is displayed in the list
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withText("Test description")).check(matches(isDisplayed()));
    }

    @Test
    public void testB_CancelAdd() {
        // Click the add item button
        onView(withId(R.id.add_item_b)).perform(click());

        onView(withId(R.id.cancel_button)).perform(click());

        // Add check that main activity shown
    }

    @Test
    public void testC_EditItem() {
        // Assuming dataList is not empty, perform an edit action on the first item in the list
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click());
        // Make changes to the item details
        onView(withId(R.id.description_edit_text)).perform(ViewActions.replaceText("Test description - Edited"), ViewActions.closeSoftKeyboard());
        // Click on the OK button for the edit
        onView(withId(R.id.ok_button)).perform(click());
        // Check if the edited item is displayed in the list
        onView(withText("Test description - Edited")).check(matches(isDisplayed()));
    }

    @Test
    public void testD_CancelEdit() {
        // Assuming dataList is not empty, perform an edit action on the first item in the list
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click());
        // Make changes to the item details
        onView(withId(R.id.description_edit_text)).perform(ViewActions.replaceText("Test description - More edits"), ViewActions.closeSoftKeyboard());
        // Click on the OK button for the edit
        onView(withId(R.id.cancel_button)).perform(click());
        // Check if the edited item is displayed in the list
        onView(withText("Test description - Edited")).check(matches(isDisplayed()));
    }

    @Test
    public void testE_DeleteItem() {
        // Assuming dataList is not empty, perform a click on the first item in the list
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click());
        // Verify if the delete option is displayed
        onView(withId(R.id.remove_button)).check(matches(isDisplayed()));
        // Click on the delete option
        onView(withId(R.id.remove_button)).perform(click());
        // Check if the deleted item is no longer displayed in the list
        onView(withText("Test description")).check(doesNotExist());
    }





}
