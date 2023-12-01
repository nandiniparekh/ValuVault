package com.example.cmput301groupproject;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhotoPickerFragment extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private List<Uri> selectedImages = new ArrayList<>();
    public void setLoadImages(List<Bitmap> loadImages) {
        this.loadImages = loadImages;
    }
    private List<Bitmap> loadImages = new ArrayList<>();

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

    }

    private void requestMediaPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private ActivityResultLauncher<String> registerForImagePicker(
            ActivityResultContracts.GetMultipleContents getMultipleContents,
            ActivityResultCallback<List<Uri>> callback) {
        return registerForActivityResult(getMultipleContents, callback);
    }

    // Create a method to handle the result
    private void handleImagePickerResult(List<Uri> uris) {
        if (uris != null) {
            selectedImages.addAll(uris);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_photos_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.photoRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        adapter = new PhotoAdapter(selectedImages);
        recyclerView.setAdapter(adapter);

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
                // Dasom's code
            }
        });

        return rootView;
    }

    public List<Uri> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(List<Uri> selectedImages) {
        this.selectedImages = selectedImages;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
        private List<Uri> imageUris;

        public PhotoAdapter(List<Uri> imageUris) {
            this.imageUris = imageUris;
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder holder, int position) {
            Uri imageUri = imageUris.get(position);
            // Load and display the image directly into the ImageView
            holder.imageView.setImageURI(imageUri);
        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        public class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public PhotoViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }
}