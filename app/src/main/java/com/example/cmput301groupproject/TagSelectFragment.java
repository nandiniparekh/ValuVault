package com.example.cmput301groupproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class TagSelectFragment extends DialogFragment {
    private ArrayList<String> tagDataList; // The list of available tags
    private ArrayList<String> selectedTags; // The list of selected tags
    private ListView tagListView;
    private TagsAdapter tagsAdapter;
    private TagsManager tagsManager;
    private OnTagsSelectedListener onTagsSelectedListener;

    public static TagSelectFragment newInstance() {
        return new TagSelectFragment();
    }

    public void setOnTagsSelectedListener(OnTagsSelectedListener listener) {
        this.onTagsSelectedListener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTagsSelectedListener) {
            onTagsSelectedListener = (OnTagsSelectedListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnTagsSelectedListener");
        }
    }

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

    // Interface to communicate the selected tags back to the calling fragment/activity
    public interface OnTagsSelectedListener {
        void onTagsSelected(ArrayList<String> selectedTags);
    }
}

