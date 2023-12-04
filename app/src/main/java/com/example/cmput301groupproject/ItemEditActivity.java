package com.example.cmput301groupproject;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * The ItemEditActivity class represents the activity for adding or editing household items.
 * It allows users to input details such as description, make, model, serial number, estimated value,
 * comment, and purchase date. Users can also select tags, associate images with the item, or scan a
 * barcode/serial number to autofill fields.
 */
public class ItemEditActivity extends AppCompatActivity implements TagSelectFragment.OnTagsSelectedListener, ScannerFragment.OnSerialNumberCapturedListener {
    private String titleDesc = "Add Item";
    private EditText description;
    private EditText make;
    private EditText model;
    private EditText serialNumber;
    private EditText estimatedValue;
    private EditText comment;
    private EditText purchaseDate;
    private ArrayList<String> selectedTags = new ArrayList<>();
    private ArrayList<Uri> images = new ArrayList<>();
    private Button selectTagsButton;
    private Button scanBarcodeButton;
    private Button scanSerialNoButton;
    private FirebaseFirestore db;
    private Button loadButton;
    private PhotoPickerFragment photoPickerFragment;
    private ArrayList<String> loadedImages;
    private List<Uri> selectedImages = new ArrayList<>();
    private ArrayList<String> imagesUpload = new ArrayList<>();

    private RecyclerView imageRecyclerView;
    private PhotoAdapter adapter;
    private HouseholdItem passedHouseholdItem;
    private GmsBarcodeScanner scanner;
    private String userCollectionPath;
    private int CAMERA_PERMISSION_CODE = 1;


    /**
     * Initializes the activity, sets up UI components, and handles user interactions.
     *
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_item_fragment);
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        if (intent != null) {
            userCollectionPath = getIntent().getStringExtra("userDoc");
        }

        // Identify UI elements
        description = findViewById(R.id.description_edit_text);
        make = findViewById(R.id.make_edit_text);
        model = findViewById(R.id.model_edit_text);
        serialNumber = findViewById(R.id.serial_number_edit_text);
        estimatedValue = findViewById(R.id.estimated_value_edit_text);
        comment = findViewById(R.id.comment_edit_text);
        purchaseDate = findViewById(R.id.purchase_date_edit_text);


        loadButton = findViewById(R.id.load_button);
        loadedImages = new ArrayList<>();

        scanSerialNoButton = findViewById(R.id.scan_serial_button);
        scanBarcodeButton = findViewById(R.id.scan_barcode_button);
        selectTagsButton = findViewById(R.id.select_tags_button);


        // Set click listeners for buttons
        scanSerialNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ItemEditActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    goToScannerFragment(false);
                }
                else{
                    requestCameraPermission();
                }
            }
        });
        scanBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ItemEditActivity.this,
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    goToScannerFragment(true);
                }
                else{
                    requestCameraPermission();
                }
            }
        });

        selectTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the TagSelectFragment
                showTagSelectFragment();
            }
        });

        // If editing an activity, receive item details
        Bundle args = intent.getExtras();
        if (args != null && args.getSerializable("selectedItem") != null) { //EDITED
            titleDesc = "Edit Item";
            passedHouseholdItem = (HouseholdItem) args.getSerializable("selectedItem");
            purchaseDate.setText(passedHouseholdItem.getDateOfPurchase());
            description.setText(passedHouseholdItem.getDescription());
            make.setText(passedHouseholdItem.getMake());
            model.setText(passedHouseholdItem.getModel());
            serialNumber.setText(passedHouseholdItem.getSerialNumber());
            estimatedValue.setText(passedHouseholdItem.getEstimatedValue());
            comment.setText(passedHouseholdItem.getComment());
            if (passedHouseholdItem.getImages() != null) {
                Log.d("item fragment already loaded", String.valueOf(passedHouseholdItem.getImages().size()));
                loadedImages = passedHouseholdItem.getImages();
            }

            onTagsSelected(passedHouseholdItem.getTags());
        }

        // Load attached items
        if (loadedImages.size() != 0) {
            for (String s : loadedImages) {
                Uri uri = Uri.parse(s);
                images.add(uri);
            }

            //initialize recycler view
            imageRecyclerView = findViewById(R.id.imageRecyclerView);
            imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            adapter = new PhotoAdapter(images, 2);
            imageRecyclerView.setAdapter(adapter);

            adapter.setOnDeleteClickListener(position -> {
                // Remove the photo from your list
                images.remove(position);
                loadedImages.remove(position);
                // Notify adapter of item removal
                adapter.notifyItemRemoved(position);
            });

        }

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Create an instance of the fragment to load
                photoPickerFragment = new PhotoPickerFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the fragment_container with the fragment
                fragmentTransaction.replace(R.id.galleryFragmentContainer, photoPickerFragment);
                fragmentTransaction.commit();
            }
        });

        TextView activityTitle = findViewById(R.id.title_text);
        activityTitle.setText(titleDesc);

        // Find the buttons
        Button okButton = findViewById(R.id.ok_button);
        Button removeButton = findViewById(R.id.remove_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // Check if the titleDesc is "Edit Item" and show/hide the remove button accordingly
        if ("Edit Item".equals(titleDesc)) {
            removeButton.setVisibility(View.VISIBLE);
        } else {
            removeButton.setVisibility(View.GONE);
        }

        // Set click listeners for buttons
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get images
                // when no images are selected, this code would break the app without the catch
                try {
                    selectedImages = photoPickerFragment.getSelectedImages();
                } catch (NullPointerException e) {
                    selectedImages.clear();
                }
                List<Task<Uri>> uploadTasks = new ArrayList<>();
                for (Uri imageUri : selectedImages) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    StorageReference imageRef = storageRef.child("images/" + UUID.randomUUID().toString());
                    UploadTask uploadTask = imageRef.putFile(imageUri);
                    Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            imagesUpload.add(downloadUri.toString());
                        } else {
                            // Handle failure
                        }
                    });
                    uploadTasks.add(urlTask);
                }

                if (loadedImages != null) {
                    imagesUpload.addAll(loadedImages);
                }

                // After loading images, attempt to submit update/add item
                Tasks.whenAllSuccess(uploadTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> list) {
                        // Get input values and update the item object
                        String desc = description.getText().toString();
                        String mk = make.getText().toString();
                        String mdl = model.getText().toString();
                        String serial = serialNumber.getText().toString();
                        String estValue = estimatedValue.getText().toString();
                        String cmt = comment.getText().toString();
                        String date = purchaseDate.getText().toString();

                        if (desc.isEmpty() || mk.isEmpty() || mdl.isEmpty() || estValue.isEmpty() || cmt.isEmpty() || date.isEmpty()) {
                            // Show an error message or toast indicating that all fields are required
                            showErrorDialog("All fields are required");
                            return;
                        }

                        // Define the maximum allowed character length
                        int maxCharacter = 100;

                        // Validate input string lengths
                        if (desc.length() > maxCharacter || mk.length() > maxCharacter || mdl.length() > maxCharacter ||
                                serial.length() > maxCharacter || estValue.length() > maxCharacter ||
                                cmt.length() > maxCharacter || date.length() > maxCharacter) {
                            // Show an error message or toast for exceeding character limit
                            showErrorDialog("Input fields must not exceed " + maxCharacter + " characters");
                            return;
                        }

                        // Validate Estimated Value
                        try {
                            // Try to convert the estimated value to double
                            double estimatedValueDouble = Double.parseDouble(estValue);
                            long maxValue = 1000000000000L;
                            // Check if the conversion is successful
                            if (estimatedValueDouble < 0 || estimatedValueDouble > maxValue) {
                                // Show an error message or toast for invalid estimated value
                                String errorMessage = "Estimated value must be a non-negative number less than " + NumberFormat.getNumberInstance(Locale.getDefault()).format(maxValue);
                                showErrorDialog(errorMessage);
                                return;
                            }
                            // Round estimated value to 2 decimal places
                            DecimalFormat df = new DecimalFormat("#.##");
                            estValue = df.format(estimatedValueDouble);

                        } catch (NumberFormatException e) {
                            // Show an error message or toast for invalid estimated value
                            showErrorDialog("Invalid estimated value format");
                            return;
                        }

                        // Validate Date Format
                        if (!isValidDate(date)) {
                            // Show an error message or toast for invalid date format
                            showErrorDialog("Invalid date entry (yyyy/MM/dd), year between 1000-2100");
                            return;
                        }

                        Intent intent = new Intent(ItemEditActivity.this, MainActivity.class);
                        intent.putExtra("userDoc", userCollectionPath);

                        // Update the passed item
                        if (passedHouseholdItem != null) {
                            passedHouseholdItem.setDescription(desc);
                            passedHouseholdItem.setMake(mk);
                            passedHouseholdItem.setModel(mdl);
                            passedHouseholdItem.setSerialNumber(serial);
                            passedHouseholdItem.setEstimatedValue(estValue);
                            passedHouseholdItem.setComment(cmt);
                            passedHouseholdItem.setDateOfPurchase(date);

                            passedHouseholdItem.setTags(selectedTags);

                            if (imagesUpload != null) {
                                int size = imagesUpload.size();
                                Log.d("ImagesUploadBottom", String.valueOf(size));
                            }
                            passedHouseholdItem.setImages(imagesUpload);

                            intent.putExtra("command", "editItem");
                            intent.putExtra("editedItem", passedHouseholdItem);
                        }
                        // Add a new item
                        else {
                            HouseholdItem newItem = new HouseholdItem(date, desc, mk, mdl, serial, estValue, cmt);
                            newItem.setTags(selectedTags);
                            newItem.setImages(imagesUpload);

                            intent.putExtra("command", "addItem");
                            intent.putExtra("addedItem", newItem);
                        }

                        startActivity(intent);
                    }
                });
            }
        });

        // Send command and item to MainActivity to be removed
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle Remove button action
                Intent intent = new Intent(ItemEditActivity.this, MainActivity.class);
                intent.putExtra("userDoc", userCollectionPath);
                intent.putExtra("command", "removeItem");
                intent.putExtra("removedItem", passedHouseholdItem);
                startActivity(intent);
            }
        });

        // Cancel operation and return to MainActivity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Return to MainActivity
                Intent intent = new Intent(ItemEditActivity.this, MainActivity.class);
                intent.putExtra("userDoc", userCollectionPath);
                startActivity(intent);
            }
        });
    }


    /**
     * Helper method to handle the permission request for camera access
     */
    private void requestCameraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("to access camera for scanning")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ItemEditActivity.this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }


    /**
     * Helper method to check if the date is in the format "yyyy/MM/dd".
     *
     * @param dateStr The date string to be validated.
     * @return True if the date is in the valid format, false otherwise.
     */
    public static boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(dateStr);

            // Check for reasonable year, month, and day entries
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Months are zero-based
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            return year >= 1000 && year <= 2100 &&
                    month >= 1 && month <= 12 &&
                    day >= 1 && day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Shows an error dialog with the specified error message.
     *
     * @param errorMessage The error message to be displayed.
     */
    private void showErrorDialog(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // You can add any specific action here if needed
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Updates the UI fields with the provided values.
     *
     * @param desc       The description to be set.
     * @param itemMake   The make to be set.
     * @param itemModel  The model to be set.
     * @param itemSerialNo The serial number to be set.
     * @param estValue   The estimated value to be set.
     * @param itemComment The comment to be set.
     * @param date       The purchase date to be set.
     */
    public void updateFields(String desc, String itemMake, String itemModel, String itemSerialNo, String estValue, String itemComment, String date) {
        if (description != null) {
            description.setText(desc);
        }
        if (make!= null) {
            make.setText(itemMake);
        }
        if (model != null) {
            model.setText(itemModel);
        }
        if (serialNumber != null) {
            serialNumber.setText(itemSerialNo);
        }
        if (estimatedValue != null) {
            estimatedValue.setText(estValue);
        }
        if (comment != null) {
            comment.setText(itemComment);
        }
        if (purchaseDate != null) {
            purchaseDate.setText(date);
        }
    }

    /**
     * Initiates the barcode scanning process using Google Code Scanner.
     *
     * @param isBarcodeScan True if scanning for barcode, false for serial number.
     */
    private void goToScannerFragment(boolean isBarcodeScan) {
        ScannerFragment scannerFragment = new ScannerFragment();
        Bundle args = new Bundle();
        args.putBoolean("CALLED_BARCODE", isBarcodeScan);
        scannerFragment.setOnSerialNumberCapturedListener(this);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        scannerFragment.setArguments(args);

        scannerFragment.show(fragmentManager, "ScannerFragment");
    }

    /**
     * Shows the TagSelectFragment for selecting tags.
     */
    private void showTagSelectFragment() {
        TagSelectFragment tagSelectFragment = new TagSelectFragment();
        tagSelectFragment.setOnTagsSelectedListener(this);

        // Use FragmentManager to open the TagSelectFragment
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        tagSelectFragment.show(fragmentManager, "TagSelectFragment");
    }

    /**
     * Handles the event when tags are selected in the TagSelectFragment.
     *
     * @param selectedTags The list of selected tags.
     */
    @Override
    public void onTagsSelected(ArrayList<String> selectedTags) {
        // Handle the selected tags here
        // This method will be called when tags are selected in TagSelectFragment
        this.selectedTags = selectedTags;


        // Find the LinearLayout in layout
        LinearLayout tagsLayout = findViewById(R.id.tags_layout);

        // Clear existing views in the layout
        tagsLayout.removeAllViews();

        // Loop through the tags and add TextViews dynamically
        for (String tag : selectedTags) {
            TextView tagTextView = new TextView(this);
            tagTextView.setText(tag);
            tagTextView.setBackgroundResource(R.drawable.tag_background);
            tagTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tagTextView.setPadding(8, 4, 8, 4);

            // Add the TextView to the LinearLayout
            tagsLayout.addView(tagTextView);
        }
    }

    /**
     * Handles the event when a serial number is captured by the ScannerFragment.
     *
     * @param serialNumber The captured serial number.
     * @param isBarcodeScan True if the capture is from barcode scanning, false for serial number.
     */
    @Override
    public void onSerialNumberCaptured(String serialNumber, boolean isBarcodeScan) {
        if (serialNumber != "") {
            accessFirebase(serialNumber, isBarcodeScan);
        }
        //getSupportFragmentManager().popBackStack();
    }

    protected void accessFirebase(String serialNo, boolean isBarcode) {
        // Query firestore for information regarding associated product
        DocumentReference docRef = db.collection("Items_Barcode_info").document(serialNo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (isBarcode == true) {
                            // Logging and updating description
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            description.setText((String) document.get("Product Description"));
                            comment.setText((String) document.get("Comment"));
                            String val = (String) document.get("Estimated Value");
                            estimatedValue.setText(val); //FIX THIS
                            serialNumber.setText(serialNo);
                            make.setText((String) document.get("Make"));
                            model.setText((String) document.get("Model"));
                            String pDate = (String) document.get("Purchase Date");
                            purchaseDate.setText(pDate);
                            Log.d("BarcodeScan used", "updated all fields");

                        }
                        else{
                            Log.d("SerialNumberScan used", "updated serial number to "+ serialNo);
                            serialNumber.setText(serialNo);
                        }
                    } else {
                        // Show an error message or toast indicating that all fields are required
                        Toast.makeText(ItemEditActivity.this, "The scanned barcode/serial number does not exist in the database.", Toast.LENGTH_SHORT).show();
                        // Logging if no such item in database
                        Log.d(TAG, "No such item exists in the database");
                    }
                } else {

                    //Logging failure message
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}