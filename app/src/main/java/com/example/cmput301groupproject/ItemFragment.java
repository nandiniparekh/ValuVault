package com.example.cmput301groupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ItemFragment extends DialogFragment {
    private String titleDesc = "Add Item";
    private EditText description;
    private EditText make;
    private EditText model;
    private EditText serialNumber;
    private EditText estimatedValue;
    private EditText comment;

    private EditText purchaseDate;

    private HouseholdItem passedHouseholdItem;
    private OnFragmentInteractionListener listener;

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

        Bundle args = getArguments();
        if (args != null) {
            titleDesc = "Edit Item";
            passedHouseholdItem = (HouseholdItem) args.getSerializable("item");
            purchaseDate.setText(passedHouseholdItem.getDateOfPurchase());
            description.setText(passedHouseholdItem.getDescription());
            make.setText(passedHouseholdItem.getMake());
            model.setText(passedHouseholdItem.getModel());
            serialNumber.setText(passedHouseholdItem.getSerialNumber());
            estimatedValue.setText(String.valueOf(passedHouseholdItem.getEstimatedValue()));
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
                        // ...
                        if (passedHouseholdItem != null) {
                            passedHouseholdItem.setDescription(desc);
                            passedHouseholdItem.setMake(mk);
                            passedHouseholdItem.setModel(mdl);
                            passedHouseholdItem.setSerialNumber(serial);
                            passedHouseholdItem.setEstimatedValue(estValue);
                            passedHouseholdItem.setComment(cmt);
                            passedHouseholdItem.setDateOfPurchase(date);

                            listener.onHouseholdItemEdited(passedHouseholdItem);
                        } else {
                            // Add a new item
                            listener.onHouseholdItemAdded(new HouseholdItem(date, desc, mk, mdl, serial, estValue, cmt));
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

    public static ItemFragment newInstance(HouseholdItem item) {
        Bundle args = new Bundle();
        args.putSerializable("item", item);

        ItemFragment fragment = new ItemFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
