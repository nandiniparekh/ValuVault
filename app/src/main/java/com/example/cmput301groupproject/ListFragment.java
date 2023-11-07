package com.example.cmput301groupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class ListFragment extends DialogFragment {
    private Button backSelectButton;
    private Button applyTagsButton;
    private Button deleteSelectedItemsButton;
    private ListView itemList;
    private ArrayList<HouseholdItem> passedItemList;
    private OnFragmentInteractionListener listener;

    public interface OnFragmentInteractionListener {
        void onTagsAdded(HouseholdItem newItem);
        void onListItemsRemoved(ArrayList<HouseholdItem> removedItems);
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_list_fragment, null);

        itemList = view.findViewById(R.id.select_item_list);

        Bundle args = getArguments();
        if (args != null) {
//            titleDesc = "Edit Item";
//            passedHouseholdItem = (HouseholdItem) args.getSerializable("item");
//            purchaseDate.setText(passedHouseholdItem.getDateOfPurchase());
//            description.setText(passedHouseholdItem.getDescription());
//            make.setText(passedHouseholdItem.getMake());
//            model.setText(passedHouseholdItem.getModel());
//            serialNumber.setText(passedHouseholdItem.getSerialNumber());
//            estimatedValue.setText(passedHouseholdItem.getEstimatedValue());
//            comment.setText(passedHouseholdItem.getComment());
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view)
                .setTitle("Choose action for selected items")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Get input values and update the item object
//                        String desc = description.getText().toString();
//                        String mk = make.getText().toString();
//                        String mdl = model.getText().toString();
//                        String serial = serialNumber.getText().toString();
//                        String estValue = estimatedValue.getText().toString();
//                        String cmt = comment.getText().toString();
//                        String date = purchaseDate.getText().toString();
//                        // ...
//                        if (passedHouseholdItem != null) {
//                            passedHouseholdItem.setDescription(desc);
//                            passedHouseholdItem.setMake(mk);
//                            passedHouseholdItem.setModel(mdl);
//                            passedHouseholdItem.setSerialNumber(serial);
//                            passedHouseholdItem.setEstimatedValue(estValue);
//                            passedHouseholdItem.setComment(cmt);
//                            passedHouseholdItem.setDateOfPurchase(date);
//
//                            listener.onHouseholdItemEdited(passedHouseholdItem);
//                        } else {
//                            // Add a new item
//                            listener.onHouseholdItemAdded(new HouseholdItem(date, desc, mk, mdl, serial, estValue, cmt));
//                        }
                    }
                });

//        if (titleDesc.equals("Edit Item")) {
//            // Removes the passed expense object from the main listview
//            builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    listener.onHouseholdItemRemoved(passedHouseholdItem);
//                }
//            });
//        }

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

