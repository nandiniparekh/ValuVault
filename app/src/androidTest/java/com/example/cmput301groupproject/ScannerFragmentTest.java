package com.example.cmput301groupproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class ScannerFragmentTest {

    private Context context;
    private ScannerFragment scannerFragment;

    @Before
    public void setUp() {
        ActivityScenario<ItemEditActivity> activityScenario = ActivityScenario.launch(ItemEditActivity.class);
        activityScenario.onActivity(activity -> {
            context = ApplicationProvider.getApplicationContext();
            scannerFragment = new ScannerFragment();
            activity.getSupportFragmentManager().beginTransaction().add(scannerFragment, null).commit();
        });
    }
    @Test
    public void test_SampleImageNumberRecognition() {
        // Click the add item button
        Bitmap sampleBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sample_image_in_db);
        if (sampleBitmap != null) {
            scannerFragment.performOCR(sampleBitmap);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertEquals("4234", scannerFragment.serialNoWoutSpaces);
        } else {
            System.err.println("Error: Bitmap is null");
            fail("Bitmap is null");
        }
    }

}

