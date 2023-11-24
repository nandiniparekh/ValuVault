package com.example.cmput301groupproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput301groupproject.HouseholdItem;
import com.example.cmput301groupproject.R;
import com.example.cmput301groupproject.adapters.SelectedItemAdapter;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private Button backSelectButton;
    private Button applyTagsButton;
    private Button deleteSelectedItemsButton;
    private ListView selectItemList;
    private ArrayList<HouseholdItem> passedDataList;
    private SelectedItemAdapter listAdapter;
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

//        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, passedDataList);
        listAdapter = new SelectedItemAdapter(this, passedDataList);
//        listAdapter = new CustomItemList(this, passedDataList);

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
