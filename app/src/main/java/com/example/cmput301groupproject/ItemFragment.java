package com.example.cmput301groupproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ItemFragment extends DialogFragment {
    private EditText description;
    private EditText make;
    private EditText model;
    private EditText serialNumber;
    private EditText estimatedValue;
    private EditText comment;

    private EditText purchaseDate;
    private OnFragmentInteractionListener listener;

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add/Edit Household Item")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialog, which) -> {
                    String desc = description.getText().toString();
                    String mk = make.getText().toString();
                    String mdl = model.getText().toString();
                    String serial = serialNumber.getText().toString();
                    String estValue = estimatedValue.getText().toString();
                    String cmt = comment.getText().toString();
                    String date = purchaseDate.getText().toString();

                    if (desc.isEmpty() || mk.isEmpty() || mdl.isEmpty() || serial.isEmpty() || cmt.isEmpty()) {
                        return;
                    }

                    listener.onOKPressed(new HouseholdItem(date, desc, mk, mdl, serial, estValue, cmt));
                }).create();
    }

    public interface OnFragmentInteractionListener {
        void onOKPressed(HouseholdItem item);
    }
}
