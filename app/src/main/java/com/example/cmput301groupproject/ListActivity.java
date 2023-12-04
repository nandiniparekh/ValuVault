package com.example.cmput301groupproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

/**
 * The ListActivity class represents the activity for displaying a list of household items.
 * Users can select and apply tags to the selected items in this activity.
 */
public class ListActivity extends AppCompatActivity implements TagSelectFragment.OnTagsSelectedListener{
    private Button backSelectButton;
    private Button applyTagsButton;
    private Button deleteSelectedItemsButton;
    private ListView selectItemList;
    private ArrayList<HouseholdItem> passedDataList;
    private SelectedItemAdapter listAdapter;
    private ArrayList<HouseholdItem> selectedItems;
    private ArrayList<String> selectedTags;
    private String userCollectionPath;

    /**
     * Called when the activity is created.
     * Initializes and sets up the UI components, adapters, and click listeners.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialize dataList and selectedItems
        passedDataList = new ArrayList<>();
        selectedItems = new ArrayList<>();

        // Get the dataList from the intent
        Intent intent = getIntent();
        if (intent != null) {
            passedDataList = (ArrayList<HouseholdItem>) intent.getSerializableExtra("dataList");
            userCollectionPath = getIntent().getStringExtra("userDoc");
        }

        // Save the userCollectionPath in MainActivity
        SharedPreferences preferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userCollectionPath", userCollectionPath);
        editor.apply();

        // Initialize and set up the ListView and adapter
        selectItemList = findViewById(R.id.select_item_list);
        backSelectButton = findViewById(R.id.backSelectButton);
        applyTagsButton = findViewById(R.id.applyTagsButton);
        deleteSelectedItemsButton = findViewById(R.id.deleteSelectedItemsButton);

        listAdapter = new SelectedItemAdapter(this, passedDataList);

        selectItemList.setAdapter(listAdapter);

        // Set up checkbox click listener for selecting and deselecting items
        listAdapter.setOnItemCheckedChangeListener(new SelectedItemAdapter.OnItemCheckedChangeListener() {
            @Override
            public void onItemCheckedChange(int position, boolean isChecked) {
                HouseholdItem selectedItem = passedDataList.get(position);
                if (isChecked && !selectedItems.contains(selectedItem)) {
                    selectedItems.add(selectedItem);
                } else if (!isChecked && selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                }
            }
        });


        backSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exit activity
                finish();
            }
        });

        applyTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send intent with selected items and tags to MainActivity
                showTagSelectFragment();
            }
        });

        deleteSelectedItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send dataList to MainActivity with command
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("selectedItems", selectedItems);
                intent.putExtra("command", "deleteItems");
                intent.putExtra("userDoc", userCollectionPath);
                startActivity(intent);
            }
        });
    }

    /**
     * Shows the TagSelectFragment to allow users to choose tags for the selected items.
     */
    private void showTagSelectFragment() {
        TagSelectFragment tagSelectFragment = new TagSelectFragment();
        tagSelectFragment.setOnTagsSelectedListener(this);

        // Use FragmentManager to open the TagSelectFragment
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        tagSelectFragment.show(fragmentManager, "TagSelectFragment");
    }

    /**
     * Called when tags are selected in the TagSelectFragment.
     * Handles the selected tags and sends an intent to the MainActivity to apply tags to selected items.
     *
     * @param chosenTags The list of selected tags.
     */
    @Override
    public void onTagsSelected(ArrayList<String> chosenTags) {
        // Sends selected items to MainActivity to be updated with selected tags
        selectedTags = chosenTags;

        Intent intent = new Intent(ListActivity.this, MainActivity.class);
        intent.putExtra("selectedItems", selectedItems);
        intent.putExtra("selectedTags", selectedTags);
        intent.putExtra("command", "applyTags");
        intent.putExtra("userDoc", userCollectionPath);
        startActivity(intent);
    }
}
