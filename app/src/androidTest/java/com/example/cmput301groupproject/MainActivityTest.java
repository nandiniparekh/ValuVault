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
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testA_AddItem() {
        // Click the add item button
        onView(withId(R.id.add_item_b)).perform(click());
        // Type in the required fields for the new item
        onView(withId(R.id.purchase_date_edit_text)).perform(ViewActions.typeText("2023/11/09"));
        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("Test description"));
        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("Test make"));
        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("Test model"));
        onView(withId(R.id.serial_number_edit_text)).perform(ViewActions.typeText("Test serial number"));
        onView(withId(R.id.estimated_value_edit_text)).perform(ViewActions.typeText("100"));
        onView(withId(R.id.comment_edit_text)).perform(ViewActions.typeText("Test comment"));
        // Click on the OK button for adding the item
        onView(withText("OK")).perform(click());
        // Check if the newly added item is displayed in the list
        onView(withText("Test description")).check(matches(isDisplayed()));
    }

    @Test
    public void testB_EditItem() {
        // Assuming dataList is not empty, perform an edit action on the first item in the list
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click());
        // Make changes to the item details
        onView(withId(R.id.description_edit_text)).perform(ViewActions.replaceText("Test description - Edited"));
        // Click on the OK button for the edit
        onView(withText("OK")).perform(click());
        // Check if the edited item is displayed in the list
        onView(withText("Test description - Edited")).check(matches(isDisplayed()));
    }

    @Test
    public void testC_DeleteItem() {
        // Assuming dataList is not empty, perform a click on the first item in the list
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(click());
        // Verify if the delete option is displayed
        onView(withText("Remove")).check(matches(isDisplayed()));
        // Click on the delete option
        onView(withText("Remove")).perform(click());
        // Check if the deleted item is no longer displayed in the list
        onView(withText("Test description")).check(doesNotExist());
    }

    @Test
    public void testD_ScanButton_from_AddItem() {
        //Assuming we are on main activity we try to test navigation to scanner
        // Click on add item
        onView(withId(R.id.add_item_b)).perform(click());
        // Click on Scan Button
        onView(withId(R.id.scan_barcode_button)).perform(click());
    }

}



//    @Test
//    public void testD_ListView() {
//        // Click the add item button
//        onView(withId(R.id.add_item_b)).perform(click());
//
//        // Type in the required fields for the new item
//        onView(withId(R.id.purchase_date_edit_text)).perform(ViewActions.typeText("2023-11-09"));
//        onView(withId(R.id.description_edit_text)).perform(ViewActions.typeText("Test description"));
//        onView(withId(R.id.make_edit_text)).perform(ViewActions.typeText("Test make"));
//        onView(withId(R.id.model_edit_text)).perform(ViewActions.typeText("Test model"));
//        onView(withId(R.id.serial_number_edit_text)).perform(ViewActions.typeText("Test serial number"));
//        onView(withId(R.id.estimated_value_edit_text)).perform(ViewActions.typeText("100"));
//        onView(withId(R.id.comment_edit_text)).perform(ViewActions.typeText("Test comment"));
//        onView(withText("OK")).perform(click());
//
//        // Check if the item is present in the ListView
//        onData(instanceOf(HouseholdItem.class))
//                .inAdapterView(withId(R.id.item_list))
//                .atPosition(0)
//                .onChildView(withId(R.id.description_edit_text))
//                .check(matches(withText("Test description")));
//    }


