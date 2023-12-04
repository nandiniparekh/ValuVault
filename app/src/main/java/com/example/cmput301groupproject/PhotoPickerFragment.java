package com.example.cmput301groupproject;


import android.Manifest;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A Fragment to pick photos from the device's gallery or capture photos using the camera.
 */
public class PhotoPickerFragment extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraImagePickerLauncher;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Uri> selectedImages = new ArrayList<>();
    private List<Uri> loadedImages = new ArrayList<>();

    /**
     * Initializes necessary components and sets up ActivityResultLaunchers for loading images
     * from gallery and camera.
     *
     * @param savedInstanceState A Bundle containing the saved state information
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted
                        openGallery();
                    }
                });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            int count;
                            if (data.getClipData() != null) {
                                count = data.getClipData().getItemCount();

                                for (int i = 0; i < count; i++) {
                                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                    selectedImages.add(imageUri);

                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );

        cameraImagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    //convert bitmap to uri and add it to selected images
                    Uri cameraImageUri = getImageUri(requireContext(), imageBitmap);
                    selectedImages.add(cameraImageUri);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    /**
     * Requests permission to access media files.
     */
    private void requestMediaPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    /**
     * Registers an ActivityResultLauncher for picking images.
     *
     * @param getMultipleContents The ActivityResultContracts.GetMultipleContents instance
     * @param callback            The callback to handle the result
     * @return The registered ActivityResultLauncher
     */
    private ActivityResultLauncher<String> registerForImagePicker(
            ActivityResultContracts.GetMultipleContents getMultipleContents,
            ActivityResultCallback<List<Uri>> callback) {
        return registerForActivityResult(getMultipleContents, callback);
    }

    /**
     * Handles the result obtained from the image picker.
     *
     * @param uris The list of URIs representing selected images
     */
    private void handleImagePickerResult(List<Uri> uris) {
        if (uris != null) {
            selectedImages.addAll(uris);
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Converts a Bitmap image captured from the camera to its corresponding Uri.
     *
     * @param inContext The Context in which the method is called
     * @param inImage   The Bitmap image to be converted
     * @return The Uri of the converted image
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        // Return the image Uri
        return Uri.parse(path);
    }

    /**
     * Getter function for selected images
     *
     * @return The list of selected images as a List of Uri objects
     */
    public List<Uri> getSelectedImages() {
        Log.d("selected images", String.valueOf(selectedImages.size()));
        if (selectedImages == null) {
            ArrayList<Uri> emptyArray = new ArrayList<>();
            return emptyArray;
        }
        return selectedImages;
    }
    /**
     * Opens the device's gallery to select images.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Inflates the layout for the Fragment and initializes RecyclerView and buttons.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the Fragment
     * @param container          The parent view that the Fragment's UI should be attached to
     * @param savedInstanceState A Bundle containing the saved state information
     * @return The inflated View for the Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_photos_fragment, container, false);


        recyclerView = rootView.findViewById(R.id.photoRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        Log.d("adapter2", String.valueOf(requireContext()));
        adapter = new PhotoAdapter(selectedImages, 1);
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener(position -> {
            // Remove the photo from your list
            selectedImages.remove(position);

            Log.d("delete selected images", String.valueOf(selectedImages.size()));
            // Notify adapter of item removal
            adapter.notifyItemRemoved(position);
        });

        Button choosePhotoButton = rootView.findViewById(R.id.choosePhotoButton);

        Button takePhotoButton = rootView.findViewById(R.id.camera_button);


        choosePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraImagePickerLauncher.launch(cameraIntent);
            }
        });

        return rootView;
    }
}

