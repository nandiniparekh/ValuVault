package com.example.cmput301groupproject;

import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ScannerFragment extends DialogFragment {

    ProcessCameraProvider cameraProvider;
    PreviewView previewView;
    private ImageCapture imageCapture;
    private Uri temporaryUri;

    public ScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        previewView = view.findViewById(R.id.previewView);
        Button captureButton = view.findViewById(R.id.btnCapture);
        captureButton.setOnClickListener(view1 -> captureImage());

        return view;


    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageCapture imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();

        // Set up the surface provider for the preview
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        try{
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        //Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview);
    }
    void captureImage() {
        // Create output options object which contains file or uri information where captured images will be saved.
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(createOutputFile())
                        .build();

        // Capture the image using the provided onClick method
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getContext()),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Image capture success. Handle the saved file results here.
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Handle the saved file results, e.g., display the image
                                // in an ImageView or navigate to the next screen.
                                // outputFileResults.getSavedUri() contains the URI of the saved image file.
                                // You can use it to display the captured image.
                                temporaryUri = outputFileResults.getSavedUri();
                                dismiss();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        // Image capture failed. Handle the error accordingly.
                        exception.printStackTrace();
                    }
                });
    }
    private File createOutputFile() {
        File outputDirectory = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "camera_capture");
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File outputFile = new File(outputDirectory, imageFileName + ".jpg");
        return outputFile;
    }

}
