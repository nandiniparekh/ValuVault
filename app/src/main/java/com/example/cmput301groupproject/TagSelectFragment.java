package com.example.cmput301groupproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

/**
 * A DialogFragment for selecting tags from a list.
 */
public class TagSelectFragment extends DialogFragment {
    private ArrayList<String> tagDataList; // The list of available tags
    private ArrayList<String> selectedTags; // The list of selected tags
    private ListView tagListView;
    private TagsAdapter tagsAdapter;
    private TagsManager tagsManager;
    private OnTagsSelectedListener onTagsSelectedListener;

    /**
     * Creates a new instance of TagSelectFragment.
     *
     * @return A new instance of TagSelectFragment.
     */
    public static TagSelectFragment newInstance() {
        return new TagSelectFragment();
    }

    /**
     * Sets the listener for tags selected events.
     *
     * @param listener The listener to be set.
     */
    public void setOnTagsSelectedListener(OnTagsSelectedListener listener) {
        this.onTagsSelectedListener = listener;
    }

    /**
     * Called when the fragment is attached to an activity. This method ensures that the
     * attached context implements the {@link OnTagsSelectedListener} interface.
     *
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTagsSelectedListener) {
            onTagsSelectedListener = (OnTagsSelectedListener) context;
        } else {
            // Log a warning instead of throwing an exception
            Log.w("TagSelectFragment", "Attached context does not implement OnTagsSelectedListener");
        }
    }

    /**
     * Called to create and return the view hierarchy associated with the fragment. This method
     * initializes the UI components, fetches tags from the TagsManager, and handles tag selection.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tags_select_fragment, container, false);

        tagDataList = new ArrayList<>();
        selectedTags = new ArrayList<>();

        tagListView = view.findViewById(R.id.tag_list);
        tagsAdapter = new TagsAdapter(getContext(), tagDataList);
        tagListView.setAdapter(tagsAdapter);

        SharedPreferences preferences = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        String userCollectionPath = preferences.getString("userCollectionPath", null);

        // Initialize TagsManager
        tagsManager = new TagsManager(userCollectionPath);

        // Fetch tags from TagsManager
        tagsManager.getTags(new TagsManager.CallbackHandler<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> tags) {
                tagsAdapter.addAll(tags);
                tagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String e) {
                // Handle failure if needed
            }
        });

        tagsAdapter.setOnCheckboxCheckedChangeListener(new TagsAdapter.OnCheckboxCheckedChangeListener() {
            @Override
            public void onCheckboxChange(int position, boolean isChecked) {
                // Handle tag selection based on checkbox changes
                String selectedTag = tagsAdapter.getItem(position);
                if (isChecked) {
                    selectedTags.add(selectedTag);
                } else {
                    selectedTags.remove(selectedTag);
                }
            }
        });

        Button applyTagsButton = view.findViewById(R.id.apply_tags_button);
        applyTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTagsSelectedListener != null) {
                    onTagsSelectedListener.onTagsSelected(selectedTags);
                }
                dismiss();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    /**
     * Interface to communicate the selected tags back to the calling fragment/activity.
     */
    public interface OnTagsSelectedListener {
        void onTagsSelected(ArrayList<String> selectedTags);
    }
}

