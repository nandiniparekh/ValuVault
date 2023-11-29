package com.example.cmput301groupproject;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.util.ArrayList;
import java.util.List;


public class ItemFragment extends DialogFragment {

    private String titleDesc = "Add Item";
    private EditText description;
    private EditText make;
    private EditText model;
    private EditText serialNumber;
    private EditText estimatedValue;
    private EditText comment;
    private Button loadButton;
    private Button camUpload_button;
    private EditText purchaseDate;
    private PhotoPickerFragment photoPickerFragment;
    private FirebaseFirestore db;
    private CollectionReference itemsRef;
    private GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                    Barcode.FORMAT_UPC_A)
            .enableAutoZoom()
            .build();
    private HouseholdItem passedHouseholdItem;
    private OnFragmentInteractionListener listener;

    private List<Uri> selectedImages = new ArrayList<>();
    private ArrayList<String> imagesUpload = new ArrayList<>();

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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_item_fragment, null);

        description = view.findViewById(R.id.description_edit_text);
        make = view.findViewById(R.id.make_edit_text);
        model = view.findViewById(R.id.model_edit_text);
        serialNumber = view.findViewById(R.id.serial_number_edit_text);
        estimatedValue = view.findViewById(R.id.estimated_value_edit_text);
        comment = view.findViewById(R.id.comment_edit_text);
        purchaseDate = view.findViewById(R.id.purchase_date_edit_text);
        loadButton = view.findViewById(R.id.load_button);
        camUpload_button = view.findViewById(R.id.cam_button);

        camUpload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getChildFragmentManager(); // Use getChildFragmentManager() for nested fragments
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                photoPickerFragment = new PhotoPickerFragment();
                transaction.replace(R.id.cameraContainer, photoPickerFragment);
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack for navigation
                transaction.commit();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getChildFragmentManager(); // Use getChildFragmentManager() for nested fragments
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Create an instance of the fragment to load
                photoPickerFragment = new PhotoPickerFragment();
//                selectedImages = photoPickerFragment.getSelectedImages();
//                String size = String.valueOf(selectedImages.size());
//                Log.d("itemfragment", size);
//
//                for (Uri uri : selectedImages) {
//                    String stringUri = uri.toString();
//                    imagesUpload.add(stringUri);
//                }

                // Replace the content of the fragmentContainer with the new fragment
                transaction.replace(R.id.galleryFragmentContainer, photoPickerFragment);
                transaction.addToBackStack(null); // Optional: Add the transaction to the back stack for navigation
                transaction.commit();
            }
        });


        Button scanBarcodeButton = view.findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(view1 -> startScanner());
        scanBarcodeButton = view.findViewById(R.id.scan_barcode_button);
        scanBarcodeButton.setOnClickListener(view1 -> startScanner());
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
                        selectedImages = photoPickerFragment.getSelectedImages();
                        String size = String.valueOf(selectedImages.size());
                        Log.d("item fragment", size);

                        for (Uri uri : selectedImages) {
                            String stringUri = uri.toString();
                            imagesUpload.add(stringUri);
                        }

                        // Get input values and update the item object
                        String desc = description.getText().toString();
                        String mk = make.getText().toString();
                        String mdl = model.getText().toString();
                        String serial = serialNumber.getText().toString();
                        String estValue = estimatedValue.getText().toString();
                        String cmt = comment.getText().toString();
                        String date = purchaseDate.getText().toString();
                        // ...
                        if (passedHouseholdItem != null) {
                            passedHouseholdItem.setDescription(desc);
                            passedHouseholdItem.setMake(mk);
                            passedHouseholdItem.setModel(mdl);
                            passedHouseholdItem.setSerialNumber(serial);
                            passedHouseholdItem.setEstimatedValue(estValue);
                            passedHouseholdItem.setComment(cmt);
                            passedHouseholdItem.setDateOfPurchase(date);
                            passedHouseholdItem.setImages(imagesUpload);

                            listener.onHouseholdItemEdited(passedHouseholdItem);
                        } else {
                            // Add a new item
                            HouseholdItem newItem = new HouseholdItem(date, desc, mk, mdl, serial, estValue, cmt);
                            newItem.setImages(imagesUpload);

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

    private void startScanner(){
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(getContext(),options);

        // The task failed with an exception
        scanner
                .startScan()
                .addOnSuccessListener(
                        barcode -> {
                            String scannedBarcode = barcode.getRawValue();
                            DocumentReference docRef = db.collection("Items_Barcode_info").document(scannedBarcode);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            description.setText((String)document.get("Product Description"));
                                        } else {
                                            Log.d(TAG, "No such item exists in the database");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        })
                .addOnCanceledListener(
                        () -> {
                            // The task has been cancelled
                        })
                .addOnFailureListener(
                        Throwable::getMessage);

    }
    public static ItemFragment newInstance(HouseholdItem item) {
        Bundle args = new Bundle();
        args.putSerializable("item", item);

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
