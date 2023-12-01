package com.example.cmput301groupproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;

public class PhotographyFragment extends Fragment {
    private Button attachButton;
    private Button deleteButton;
    private Button btnPicture;
    private ImageView imageView;

    public Uri getImageUri() {
        return imageUri;
    }

    private Uri imageUri;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photography, container, false);

        attachButton = view.findViewById(R.id.attach_button);
        deleteButton = view.findViewById(R.id.delete_button);
        btnPicture = view.findViewById(R.id.btncamera);
        imageView = view.findViewById(R.id.image);


        attachButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);

        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(cameraIntent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
            }
        });

        attachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                    imageUri = getImageUri(requireContext(), imageBitmap);
                    toggleButtonsVisibility(view);

                }
            }
        });
        return view;
    }
    private void toggleButtonsVisibility(View view) {
        if (view.getVisibility()==View.INVISIBLE) {
            attachButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            btnPicture.setVisibility(View.VISIBLE);
        } else {
            attachButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            btnPicture.setVisibility(View.INVISIBLE);
        }
    }
    private void resetView() {
        // Reset the view to its initial state
        attachButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        btnPicture.setVisibility(View.VISIBLE);
        imageView.setImageDrawable(null);  // Clear the image

        // Additional cleanup if needed
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.d("here", "hi3");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.d("here", "hi4");
        // Return the image Uri
        return Uri.parse(path);
    }

}
