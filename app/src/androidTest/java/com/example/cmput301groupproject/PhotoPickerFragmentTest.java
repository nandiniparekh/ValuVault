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
import android.provider.MediaStore;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PhotoPickerFragmentTest extends TestCase {

    @Rule
    public ActivityScenarioRule<PhotoPickerFragment> activityRule =
            new ActivityScenarioRule<>(PhotoPickerFragment.class);

    @Test
    public void testTakePhoto() {
        // Click the "Take Photo" button
        onView(withId(R.id.camera_button)).perform(click());

        // Verify that the camera intent is launched
        intended(hasAction(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    @Test
    public void testOpenGallery() {
        // Click the "Choose Photo" button
        onView(withId(R.id.choosePhotoButton)).perform(click());

        // Verify that the gallery intent is launched
        intended(allOf(
                hasAction(Intent.ACTION_GET_CONTENT),
                hasType("image/*"),
                hasExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)));
        }
    }

