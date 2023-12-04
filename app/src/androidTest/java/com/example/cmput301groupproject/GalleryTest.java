package com.example.cmput301groupproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

public class GalleryTest {
    @RunWith(AndroidJUnit4.class)
    public class PhotoPickerFragmentTest {
        @Rule
        public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
        @Test
        public void testOpenGallery() {
            // Click the "Choose Photo" button
            onView(withId(R.id.choosePhotoButton)).perform(click());
            // Verify that the gallery intent is launched
            intended(allOf(
                    hasAction(Intent.ACTION_GET_CONTENT),
                    hasType("image/*"),
                    hasExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            ));
        }
    }
}
