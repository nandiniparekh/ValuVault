package com.example.cmput301groupproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmput301groupproject.R;
import com.example.cmput301groupproject.adapters.TagsAdapter;
import com.example.cmput301groupproject.fragments.AddTagsFragment;

import java.util.ArrayList;

public class ViewTagsActivity extends AppCompatActivity implements AddTagsFragment.TagsListener {

    private TagsManager tagsManager;
    private ArrayList<String> tagList;
    private RecyclerView recyclerView;
    private TagsAdapter tagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tags);

        // Retrieve userCollectionPath from the intent
        String userCollectionPath = getIntent().getStringExtra("userID");

        // Initialize TagsManager with the userCollectionPath
        tagsManager = new TagsManager(userCollectionPath);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTags);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        tagsAdapter = new TagsAdapter(tagList);
//        recyclerView.setAdapter(tagsAdapter);
//
//        // Fetch tags from the database
//        fetchTags();

        // Set up button click listeners
        Button btnAddTags = findViewById(R.id.btnAddTags);
        btnAddTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Add Tags button click
                AddTagsFragment addTagsFragment = AddTagsFragment.newInstance(tagList);
                addTagsFragment.show(getSupportFragmentManager(), "addTagsDialog");
            }
        });

        Button btnDeleteTags = findViewById(R.id.btnDeleteTags);
        btnDeleteTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle Delete Selected Tags button click
                deleteSelectedTags();
            }
        });

        // Add the back button click listener
        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(new View.OnClickListener() {
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
                tagList = tags;
                tagsAdapter.setTags(tagList);
            }

            @Override
            public void onFailure(String e) {
                // Handle failure if needed
            }
        });
    }

    private void deleteSelectedTags() {
        // Get the list of selected tags from the adapter
        ArrayList<String> selectedTags = tagsAdapter.getSelectedTags();

        // Delete the selected tags using TagsManager
        tagsManager.deleteTags(selectedTags, new TagsManager.CallbackHandler<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> deletedTags) {
                // Update the local tagList and notify the adapter
                tagList.removeAll(deletedTags);
                tagsAdapter.setTags(tagList);
            }

            @Override
            public void onFailure(String e) {
                // Handle failure if needed
            }
        });
    }

    // Implement the TagsListener method to handle added tags
    @Override
    public void onTagsAdded(ArrayList<String> newTags) {
        // Update the tagList and notify the adapter
        tagList.addAll(newTags);
        tagsAdapter.setTags(tagList);
    }
}

