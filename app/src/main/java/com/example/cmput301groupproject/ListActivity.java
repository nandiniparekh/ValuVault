package com.example.cmput301groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private Button backSelectButton;
    private Button applyTagsButton;
    private Button deleteSelectedItemsButton;
    private ListView selectItemList;
    private ArrayList<HouseholdItem> passedDataList;
    private ArrayAdapter<HouseholdItem> listAdapter;
    private ArrayList<HouseholdItem> selectedItems;
    private ArrayList<String> selectedTags;
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
        }

        // Initialize and set up the ListView and adapter
        selectItemList = findViewById(R.id.select_item_list);
        backSelectButton = findViewById(R.id.backSelectButton);
        applyTagsButton = findViewById(R.id.applyTagsButton);
        deleteSelectedItemsButton = findViewById(R.id.deleteSelectedItemsButton);

        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, passedDataList);

        selectItemList.setAdapter(listAdapter);

        // Set up item click listener for selecting and deselecting items
        selectItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HouseholdItem selectedItem = passedDataList.get(position);
                if (selectedItems.contains(selectedItem)) {
                    selectedItems.remove(selectedItem);
                } else {
                    selectedItems.add(selectedItem);
                }

                listAdapter.notifyDataSetChanged(); // Notify the adapter of the data set changes
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
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("selectedItems", selectedItems);
                intent.putExtra("selectedTags", selectedTags);
                intent.putExtra("command", "applyTags");
                startActivity(intent);
            }
        });

        deleteSelectedItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send dataList to MainActivity with command
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                intent.putExtra("selectedItems", selectedItems);
                intent.putExtra("command", "deleteItems");
                startActivity(intent);
            }
        });
    }

}
