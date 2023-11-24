package com.example.cmput301groupproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput301groupproject.R;
import com.example.cmput301groupproject.adapters.TagsAdapter;
import com.example.cmput301groupproject.fragments.AddTagsFragment;
import com.example.cmput301groupproject.utility.TagsManager;

import java.util.ArrayList;

public class ViewTagsActivity extends AppCompatActivity implements AddTagsFragment.TagsOnFragmentInteractionListener {

    private Button backButton;
    private Button addTagButton;
    private Button deleteSelectedTagsButton;
    private TagsManager tagsManager;
    private ArrayList<String> tagDataList;
    private ArrayList<String> selectedTags;
    private ListView viewTagList;
    private TagsAdapter tagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tags);

        selectedTags = new ArrayList<>();
        tagDataList = new ArrayList<>();

        // Retrieve userCollectionPath from the intent
        String userCollectionPath = getIntent().getStringExtra("userID");

        // Initialize TagsManager with the userCollectionPath
        tagsManager = new TagsManager(userCollectionPath);

        // Set up ListView
        viewTagList = findViewById(R.id.select_tag_list);
        tagsAdapter = new TagsAdapter(this, tagDataList);
        viewTagList.setAdapter(tagsAdapter);

        // Set up checkbox click listener for selecting and deselecting items
        tagsAdapter.onTagCheckedChangeListener(new TagsAdapter.onTagCheckedChangeListener() {
            @Override
            public void onTagCheckedChange(int position, boolean isChecked) {
                String selectedTag = tagDataList.get(position);
                if (isChecked && !selectedTags.contains(selectedTag)) {
                    selectedTags.add(selectedTag);
                } else if (!isChecked && selectedTags.contains(selectedTag)) {
                    selectedTags.remove(selectedTag);
                }
            }
        });

        // Fetch tags from the database
        fetchTags();

        backButton = findViewById(R.id.btnBackToMain);
        addTagButton = findViewById(R.id.btnAddTags);
        deleteSelectedTagsButton = findViewById(R.id.btnDeleteTags);

        // Set up button click listeners
        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Tags button click
                AddTagsFragment.newInstance(tagDataList).show(getSupportFragmentManager(), "ADD_TAGS");
            }
        });

        // Delete selected tags button
        deleteSelectedTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Delete Selected Tags button click
                deleteSelectedTags();
            }
        });

        // Add the back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to MainActivity
                Intent intent = new Intent(ViewTagsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchTags() {
        tagsManager.getTags(new TagsManager.CallbackHandler<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> tags) {
                tagDataList.clear();
                tagDataList.addAll(tags);
                tagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String e) {
                // Handle failure if needed
            }
        });
    }

    private void deleteSelectedTags() {
        // Delete the selected tags using TagsManager
        tagsManager.deleteTags(selectedTags, new TagsManager.CallbackHandler<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> deletedTags) {
                // Update the local tagList and notify the adapter
                tagDataList.removeAll(deletedTags);
                tagsAdapter.notifyDataSetChanged();

                // Clear the selected tags list
                selectedTags.clear();

                tagsAdapter.clearAllCheckedItems();
            }

            @Override
            public void onFailure(String e) {
                // Handle failure if needed
            }
        });
    }

    // Implement the TagsListener method to handle added tags
    public void onTagAdded(String newTag) {
        // Update the Firestore database with the new list of tags
        tagsManager.addTag(newTag, new TagsManager.CallbackHandler<String>() {
            @Override
            public void onSuccess(String result) {
                // Update the tagList and notify the adapter
                tagDataList.add(newTag);
                tagsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String e) {
                // Handle failure if needed
                Log.e("TagsManager", "Error adding tag in Firestore: " + e);
            }
        });
    }
}

