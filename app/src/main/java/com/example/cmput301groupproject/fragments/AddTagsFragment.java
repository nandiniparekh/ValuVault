package com.example.cmput301groupproject.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301groupproject.R;

import java.util.ArrayList;

public class AddTagsFragment extends DialogFragment {

    private static final String ARG_TAG_LIST = "tagList";

    private ArrayList<String> tagList;

    public static AddTagsFragment newInstance(ArrayList<String> tagList) {
        AddTagsFragment fragment = new AddTagsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TAG_LIST, tagList);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tagList = getArguments().getStringArrayList(ARG_TAG_LIST);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.tags_fragment, null);

        final EditText editTextTag = view.findViewById(R.id.editTextTag);
        Button addButton = view.findViewById(R.id.btnAddTag);
        Button cancelButton = view.findViewById(R.id.btnCancel);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTag = editTextTag.getText().toString().trim();
                if (!newTag.isEmpty()) {
                    tagList.add(newTag);
                    ((TagsListener) requireActivity()).onTagsAdded(tagList);
                    dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Cancel operation and close the dialog
            }
        });

        builder.setView(view);
        return builder.create();
    }

    public interface TagsListener {
        void onTagsAdded(ArrayList<String> newTags);
    }
}
