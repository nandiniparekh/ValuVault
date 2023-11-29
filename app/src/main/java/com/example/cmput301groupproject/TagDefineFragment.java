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

import java.util.ArrayList;

public class TagDefineFragment extends DialogFragment {

    private static final String ARG_TAG_LIST = "tagList";

    private ArrayList<String> tagList;
    private EditText editTextTag;

    private TagsOnFragmentInteractionListener tagsListener;

    public interface TagsOnFragmentInteractionListener {
        void onTagAdded(String newTag);
        //void onTagRemoved(String removedTag);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TagsOnFragmentInteractionListener) {
            tagsListener = (TagsOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "TagsOnFragmentInteractionListener is not implemented");
        }
    }

    public static TagDefineFragment newInstance(ArrayList<String> tagList) {
        TagDefineFragment fragment = new TagDefineFragment();
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
        else{
            tagList = new ArrayList<>();
        }

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.tags_add_fragment, null);

        editTextTag = view.findViewById(R.id.editTextTag);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setView(view)
                .setTitle("Add Tag")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newTag = editTextTag.getText().toString().trim();
                        if (!newTag.isEmpty()) {
                            tagsListener.onTagAdded(newTag);
                        }
                        dismiss();
                    }
                });
        return builder.create();
    }


}
