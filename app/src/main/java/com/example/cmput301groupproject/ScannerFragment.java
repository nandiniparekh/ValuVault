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
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
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

/**
 * A fragment that allows capturing images using the device camera and performing OCR or barcode scanning on the captured images.
 */
public class ScannerFragment extends DialogFragment {
    ProcessCameraProvider cameraProvider;
    PreviewView previewView;
    Preview preview;
    private ImageCapture imageCapture;
    private OnSerialNumberCapturedListener serialNumberListener;
    protected String serialNoWoutSpaces;

    private boolean isBarcode;

    /**
     * Default constructor for the ScannerFragment.
     */
    public ScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is created. Initializes the camera provider and binds the camera preview.
     *
     * @param savedInstanceState A Bundle containing the saved instance state.
     */
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

    /**
     * Called to create the view for this fragment. Inflates the layout, retrieves arguments, and sets up UI components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A Bundle containing the saved instance state.
     * @return The inflated View for this fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        Bundle args = getArguments();
        if (args != null) {
            isBarcode = args.getBoolean("CALLED_BARCODE");
            Log.e("Bundle Argument Check", "" + isBarcode);
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        previewView = view.findViewById(R.id.previewView);

        // Find the capture button by ID
        Button captureButton = view.findViewById(R.id.btnCapture);

        // Set a click listener for the capture button
        captureButton.setOnClickListener(v -> captureImage(isBarcode));

        // Jaskeer's tests, uncomment individually
        //testOCROnSampleImage();
        //testBarcodeScannerOnSampleBarcode();
        //testOCROnSampleImage2();

        return view;
    }

    /**
     * Binds the camera preview to the fragment.
     *
     * @param cameraProvider The camera provider to bind.
     */
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
    }

    /**
     * Captures an image using the device camera and processes it based on the specified mode (OCR or barcode scanning).
     *
     * @param isBarcode A boolean indicating whether to perform barcode scanning.
     */
    private void captureImage(boolean isBarcode) {
        File photoFile = createTempFile();
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                // Process the captured image and extract serial number
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                if (isBarcode == false) {
                    performOCR(bitmap);
                }
                if (isBarcode == true){
                    performBarcodeScanning(bitmap);
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Creates a temporary file for saving captured images.
     *
     * @return The created temporary file.
     */
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

    /**
     * Performs OCR (Optical Character Recognition) on the provided bitmap image.
     *
     * @param bitmap The bitmap image to process.
     */
    protected void performOCR(Bitmap bitmap) {

        // Initialize TextRecognizer
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image_in_db);

        // Convert Bitmap to InputImage
        InputImage inputImage = InputImage.fromBitmap(sampleBitmap, 0);

        // Process the image using ML Kit OCR
        Task<Text> result = recognizer.process(inputImage)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Extract recognized text
                        Text text = task.getResult();
                        //processRecognizedText(text);
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
    }

    /**
     * Processes the recognized text extracted by OCR.
     *
     * @param text The recognized text.
     */
    private void processRecognizedText(Text text) {
        // Process the recognized text (extract serial number, etc.)
        // You can access individual text blocks, lines, and elements using text.getTextBlocks(), text.getLines(), text.getElements()
        List<Text.TextBlock> textBlocks = text.getTextBlocks();
        // CHANGE 1
        if (textBlocks.isEmpty()) {
            // Log a message when no text is recognized
            Log.d("MLKit", "No text recognized");
            Toast.makeText(requireContext(), "No text found in the image", Toast.LENGTH_SHORT).show();
            //serialNumberListener.onSerialNumberCaptured("");
        }
        //END CHANGE 1
        String serialNo = "";
        for (Text.TextBlock block : textBlocks) {
            String blockText = block.getText();
            serialNo += blockText;
            // Process each block of text as needed
        }
        String serialNoNoSpaces = serialNo.replace(" ", "");
        if (serialNumberListener != null && serialNoNoSpaces != "") {
            Toast.makeText(requireContext(), serialNoNoSpaces, Toast.LENGTH_SHORT).show();
            serialNoWoutSpaces = serialNoNoSpaces;
            serialNumberListener.onSerialNumberCaptured(serialNoNoSpaces, false);
            dismiss();
        }
        serialNoWoutSpaces = serialNoNoSpaces;
    }

    /**
     * Performs barcode scanning on the provided bitmap image.
     *
     * @param bitmap The bitmap image to process.
     * @return A Task containing the result of barcode scanning.
     */
    public Task<String> performBarcodeScanning(Bitmap bitmap) {
        // Create an InputImage from the bitmap
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_barcode);

        InputImage image = InputImage.fromBitmap(sampleBitmap, 0);

        // Set up the barcode scanner options to recognize only UPC-A barcodes
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_UPC_A)
                        .build();

        // Get an instance of the barcode scanner
        BarcodeScanner scanner = BarcodeScanning.getClient(options);

        // Use the barcode scanner to process the image
        return scanner.process(image)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        Log.e("BarcodeScanner", "Barcode scan attempted");

                        // Handle successful barcode scanning
                        List<Barcode> barcodes = task.getResult();
                        if (barcodes != null && !barcodes.isEmpty()) {
                            // Only handle the first barcode for simplicity
                            Barcode firstBarcode = barcodes.get(0);
                            Log.e("BarcodeScanner", "Barcode scan successful with: " + firstBarcode.getRawValue());
                            Toast.makeText(requireContext(), firstBarcode.getRawValue(), Toast.LENGTH_SHORT).show();
                            serialNumberListener.onSerialNumberCaptured(firstBarcode.getRawValue(), true);
                            dismiss();
                            return firstBarcode.getRawValue();
                        } else {
                            // No UPC-A barcode found
                            Log.e("BarcodeScanner", "No UPC-A barcode found");
                            Toast.makeText(requireContext(), "No barcode detected", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    } else {
                        // Handle the failure case
                        Toast.makeText(requireContext(), "Barcode scanning failed", Toast.LENGTH_SHORT).show();
                        Log.e("BarcodeScanner", "Barcode scanning failed: " + task.getException().getMessage());
                        return null;
                    }
                });
    }

    /**
     * Interface for capturing serial numbers.
     */
    public interface OnSerialNumberCapturedListener {
        void onSerialNumberCaptured(String serialNumber, boolean isBarcodeScan);
    }

    /**
     * Sets the listener for capturing serial numbers.
     *
     * @param listener The listener to set.
     */
    public void setOnSerialNumberCapturedListener(OnSerialNumberCapturedListener listener) {
        this.serialNumberListener = listener;
    }
    
    /**
     * Called when the view is destroyed. Unbinds the camera provider and releases resources.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unbind the camera provider and release resources
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    protected void testOCROnSampleImage() {
        // Load a sample image from resources (assuming it's in the res/drawable directory)
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image_in_db);

        // Call performOCR with the sample image
        performOCR(sampleBitmap);
    }

    private void testOCROnSampleImage2(){
        // Load a sample image from resources (assuming it's in the res/drawable directory)
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_image_2);

        // Call performOCR with the sample image
        performOCR(sampleBitmap);
    }
    private void testBarcodeScannerOnSampleBarcode() {
        // Load a sample image from resources (assuming it's in the res/drawable directory)
        Bitmap sampleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_barcode);

        // Call performOCR with the sample image
        performBarcodeScanning(sampleBitmap);
    }
}
