package com.example.cmput301groupproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.provider.MediaStore;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

public class CameraTest {
    @RunWith(AndroidJUnit4.class)
    public class PhotoPickerFragmentTest {
        @Rule
        public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
        @Test
        public void testTakePhoto() {
            // Click the "Take Photo" button
            onView(withId(R.id.camera_button)).perform(click());
            // Verify that the camera intent is launched
            intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
        }
    }
}
