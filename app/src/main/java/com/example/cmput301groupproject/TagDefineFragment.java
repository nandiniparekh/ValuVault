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

/**
 * The TagDefineFragment class represents a dialog fragment for defining and adding new tags.
 * Users can input a new tag in the provided EditText, and the tag is added when the "OK" button is pressed.
 */
public class TagDefineFragment extends DialogFragment {

    private static final String ARG_TAG_LIST = "tagList";

    private ArrayList<String> tagList;
    private EditText editTextTag;

    private TagsOnFragmentInteractionListener tagsListener;

    /**
     * Interface for communication between the TagDefineFragment and its hosting activity.
     * The hosting activity must implement this interface to handle tag-related interactions.
     */
    public interface TagsOnFragmentInteractionListener {
        void onTagAdded(String newTag);
        //void onTagRemoved(String removedTag);
    }

    /**
     * Called when the fragment is attached to the hosting activity.
     * Validates that the hosting activity implements the TagsOnFragmentInteractionListener interface.
     *
     * @param context The context of the hosting activity.
     * @throws RuntimeException if the hosting activity does not implement TagsOnFragmentInteractionListener.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TagsOnFragmentInteractionListener) {
            tagsListener = (TagsOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "TagsOnFragmentInteractionListener is not implemented");
        }
    }

    /**
     * Creates a new instance of TagDefineFragment with the provided list of existing tags.
     *
     * @param tagList The list of existing tags.
     * @return A new instance of TagDefineFragment.
     */
    public static TagDefineFragment newInstance(ArrayList<String> tagList) {
        TagDefineFragment fragment = new TagDefineFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_TAG_LIST, tagList);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates and returns the dialog for the fragment.
     * Inflates the layout and sets up the dialog's appearance and behavior.
     *
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return The created dialog.
     */
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
