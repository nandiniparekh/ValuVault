package com.example.cmput301groupproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ScannerFragment extends DialogFragment {

    ProcessCameraProvider cameraProvider;
    PreviewView previewView;
    Preview preview;

    private ImageCapture imageCapture;

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

        // Find the capture button by ID
        Button captureButton = view.findViewById(R.id.btnCapture);

        // Set a click listener for the capture button
        captureButton.setOnClickListener(v -> captureImage());

        return view;
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        preview = new Preview.Builder()
                .build();

        imageCapture = new ImageCapture.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

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

    private void captureImage() {
        File photoFile = createTempFile();
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Process the captured image and extract serial number
                processCapturedImage(photoFile);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }

    private File createTempFile() {
        // Create a temporary file for saving captured images
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getCacheDir();

        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void processCapturedImage(File photoFile) {
        // Process the captured image (e.g., extract serial number)
        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

        // TODO: Implement your image processing logic here
        // For example, you can use OCR libraries to extract text (serial number) from the image
        // Note: This is a placeholder, and you should replace it with your actual image processing code

        // Example: Extracting a simple text using OCR
        String serialNumber = performOCR(bitmap);

        // Now, you can use the extracted serial number as needed
        if (serialNumber != null && !serialNumber.isEmpty()) {
            // Update your UI or perform further actions based on the extracted serial number
            // For example, you can display the serial number in a TextView
            // textViewSerialNumber.setText(serialNumber);
        } else {
            // Handle the case where the serial number extraction failed
        }
    }

    private String performOCR(Bitmap bitmap) {
        // Initialize TextRecognizer
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        // Convert Bitmap to InputImage
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

        // Process the image using ML Kit OCR
        Task<Text> result = recognizer.process(inputImage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Extract recognized text
                        Text text = task.getResult();
                        processRecognizedText(text);
                    } else {
                        // Handle OCR failure
                        Exception e = task.getException();
                        if (e instanceof MlKitException) {
                            // Handle ML Kit exceptions
                            Log.e("MLKit", "ML Kit exception: " + e.getMessage());
                        } else {
                            // Handle other exceptions
                            Log.e("MLKit", "Unexpected exception: " + e.getMessage());
                        }
                    }
                });

        return ""; // Placeholder, actual result will be processed in the onCompleteListener
    }

    private void processRecognizedText(Text text) {
        // Process the recognized text (extract serial number, etc.)
        // You can access individual text blocks, lines, and elements using text.getTextBlocks(), text.getLines(), text.getElements()
        List<Text.TextBlock> textBlocks = text.getTextBlocks();
        for (Text.TextBlock block : textBlocks) {
            String blockText = block.getText();
            // Process each block of text as needed

            Toast.makeText(requireContext(), blockText, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unbind the camera provider and release resources
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }
}
