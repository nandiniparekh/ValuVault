package com.example.cmput301groupproject;
import android.Manifest;
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
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhotoPickerFragment extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> cameraimagePickerLauncher;
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

        cameraimagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Log.d("here", "hi3");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        Log.d("here", "hi4");
        // Return the image Uri
        return Uri.parse(path);
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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraimagePickerLauncher.launch(cameraIntent);
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
        public void onBindViewHolder(@NonNull PhotoViewHolder holder, @SuppressLint("RecyclerView") int position) {

            Uri imageUri = imageUris.get(position);
            // Load and display the image directly into the ImageView
            holder.imageView.setImageURI(imageUri);
//            Glide.with(requireContext())
//                    .load(imageUri)
//                    .downsample(DownsampleStrategy.CENTER_INSIDE) // or other DownsampleStrategy options
//                    .into(holder.imageView);

            if (holder.imageView != null) {
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        imageUris.remove(imageUri);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return imageUris.size();
        }

        public class PhotoViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView, delete;

            public PhotoViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView); // Correctly finds the imageView
                delete = itemView.findViewById(R.id.delete); // Correctly finds the delete ImageView
                // The above line is crucial. It correctly references 'delete' as a direct child of itemView (ConstraintLayout)
                Log.d("PhotoViewHolder", "imageView: " + imageView + ", delete: " + delete);
            }

        }
    }
}