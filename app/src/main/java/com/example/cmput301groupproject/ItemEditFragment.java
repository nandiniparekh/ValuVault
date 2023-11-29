package com.example.cmput301groupproject;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ItemEditFragment extends DialogFragment implements TagSelectFragment.OnTagsSelectedListener {
    private String titleDesc = "Add Item";
    private EditText description;
    private EditText make;
    private EditText model;
    private EditText serialNumber;
    private EditText estimatedValue;
    private EditText comment;
    private EditText purchaseDate;
    private ArrayList<String> selectedTags = new ArrayList<>();
    private Button selectTagsButton;
    private Button scanBarcodeButton;
    private FirebaseFirestore db;

    // Setting the configuration for BarcodeScanner as UPC-A format and enabling autozoom features
    private GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                    Barcode.FORMAT_UPC_A)
            .enableAutoZoom()
            .build();
    private HouseholdItem passedHouseholdItem;
    private OnFragmentInteractionListener listener;
    private GmsBarcodeScanner scanner;

    public interface OnFragmentInteractionListener {
        void onHouseholdItemAdded(HouseholdItem newItem);

        void onHouseholdItemEdited(HouseholdItem editedItem);

        void onHouseholdItemRemoved(HouseholdItem removedItem);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "OnFragmentInteractionListener is not implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_item_fragment, null);

        description = view.findViewById(R.id.description_edit_text);
        make = view.findViewById(R.id.make_edit_text);
        model = view.findViewById(R.id.model_edit_text);
        serialNumber = view.findViewById(R.id.serial_number_edit_text);
        estimatedValue = view.findViewById(R.id.estimated_value_edit_text);
        comment = view.findViewById(R.id.comment_edit_text);
        purchaseDate = view.findViewById(R.id.purchase_date_edit_text);

        scanBarcodeButton = view.findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(view1 -> startScanner());

        selectTagsButton = view.findViewById(R.id.select_tags_button);
        selectTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the TagSelectFragment
                showTagSelectFragment();
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            titleDesc = "Edit Item";
            passedHouseholdItem = (HouseholdItem) args.getSerializable("item");
            purchaseDate.setText(passedHouseholdItem.getDateOfPurchase());
            description.setText(passedHouseholdItem.getDescription());
            make.setText(passedHouseholdItem.getMake());
            model.setText(passedHouseholdItem.getModel());
            serialNumber.setText(passedHouseholdItem.getSerialNumber());
            estimatedValue.setText(passedHouseholdItem.getEstimatedValue());
            comment.setText(passedHouseholdItem.getComment());
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view)
                .setTitle(titleDesc)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get input values and update the item object
                        String desc = description.getText().toString();
                        String mk = make.getText().toString();
                        String mdl = model.getText().toString();
                        String serial = serialNumber.getText().toString();
                        String estValue = estimatedValue.getText().toString();
                        String cmt = comment.getText().toString();
                        String date = purchaseDate.getText().toString();

                        // Validate input fields
                        if (desc.isEmpty() || mk.isEmpty() || mdl.isEmpty() || estValue.isEmpty() || cmt.isEmpty() || date.isEmpty()) {
                            // Show an error message or toast indicating that all fields are required
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Error")
                                    .setMessage("All fields are required")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            return;
                        }

                        // Validate Estimated Value
                        try {
                            // Try to convert the estimated value to double
                            double estimatedValueDouble = Double.parseDouble(estValue);
                            // Check if the conversion is successful
                            if (estimatedValueDouble < 0) {
                                // Show an error message or toast for invalid estimated value
                                new AlertDialog.Builder(getContext())
                                        .setTitle("Error")
                                        .setMessage("Estimated value must be a non-negative number")
                                        .setPositiveButton(android.R.string.ok, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                return;
                            }
                            // Round estimated value to 2 decimal places
                            DecimalFormat df = new DecimalFormat("#.##");
                            estValue = df.format(estimatedValueDouble);

                        } catch (NumberFormatException e) {
                            // Show an error message or toast for invalid estimated value
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Error")
                                    .setMessage("Invalid estimated value format")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            return;
                        }

                        // Validate Date Format
                        if (!isValidDate(date)) {
                            // Show an error message or toast for invalid date format
                            new AlertDialog.Builder(getContext())
                                    .setTitle("Error")
                                    .setMessage("Invalid date entry (yyyy/MM/dd), year between 1000-2100")
                                    .setPositiveButton(android.R.string.ok, null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                            return;
                        }

                        if (passedHouseholdItem != null) {
                            passedHouseholdItem.setDescription(desc);
                            passedHouseholdItem.setMake(mk);
                            passedHouseholdItem.setModel(mdl);
                            passedHouseholdItem.setSerialNumber(serial);
                            passedHouseholdItem.setEstimatedValue(estValue);
                            passedHouseholdItem.setComment(cmt);
                            passedHouseholdItem.setDateOfPurchase(date);

                            passedHouseholdItem.setTags(selectedTags);

                            listener.onHouseholdItemEdited(passedHouseholdItem);
                        } else {
                            // Add a new item
                            HouseholdItem newItem = new HouseholdItem(date, desc, mk, mdl, serial, estValue, cmt);
                            newItem.setTags(selectedTags);

                            listener.onHouseholdItemAdded(newItem);
                        }
                    }
                });

        if (titleDesc.equals("Edit Item")) {
            // Removes the passed expense object from the main listview
            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    listener.onHouseholdItemRemoved(passedHouseholdItem);
                }
            });
        }

        return builder.create();
    }

    // Helper method to check if the date is in the format "yyyy/MM/dd"
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
     * This method initiates the barcode scanning process using Google Code Scanner. When a barcode
     * is scanned, the firestore database is queried for returning the associated product description
     * inside the "description" EditText when adding/editing items. In the future, it will also
     * include other information about the item.
     *
     * This method uses the GmsBarcodeScanning.getClient method to obtain an instance of the GmsBarcode
     * Scanner while configuring the barcode format to be UPC-A. It then starts the process of scanning
     * and sets up listeners in events of a successful scan and a cancelled scan. Upon success, it
     * retrieves information from the firestore collection and updates the "description" EditText.
     */
    protected void startScanner(){
        // Initialize barcode scanner
        scanner = GmsBarcodeScanning.getClient(getContext(),options);
        // Start scanning
        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            // Get the raw value of barcode scanned
                            String scannedBarcode = barcode.getRawValue();

                            // Query firestore for information regarding associated product
                            DocumentReference docRef = db.collection("Items_Barcode_info").document(scannedBarcode);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {

                                            // Logging and updating description
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            description.setText((String)document.get("Product Description"));
                                        } else {
                                            // Logging if no such item in database
                                            Log.d(TAG, "No such item exists in the database");
                                        }
                                    } else {

                                        //Logging failure message
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        })
                .addOnCanceledListener(
                        () -> {
                            // The task has been cancelled
                        });
    }

    private void showTagSelectFragment() {
        TagSelectFragment tagSelectFragment = new TagSelectFragment();
        tagSelectFragment.setOnTagsSelectedListener(this);

        // Use FragmentManager to open the TagSelectFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        tagSelectFragment.show(fragmentManager, "TagSelectFragment");
    }

    // Implement the OnTagsSelectedListener interface
    @Override
    public void onTagsSelected(ArrayList<String> selectedTags) {
        // Handle the selected tags here
        // This method will be called when tags are selected in TagSelectFragment
        this.selectedTags = selectedTags;
    }

    public static ItemEditFragment newInstance(HouseholdItem item) {
        Bundle args = new Bundle();
        args.putSerializable("item", item);

        ItemEditFragment fragment = new ItemEditFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
