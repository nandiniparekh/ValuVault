package com.example.cmput301groupproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SelectedItemAdapter extends ArrayAdapter<HouseholdItem> {
    private final Context context;
    private ArrayList<HouseholdItem> items;
    private boolean[] checkedPositions;

    public SelectedItemAdapter(Context context, ArrayList<HouseholdItem> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.selected_item_display_content, parent, false);
        }

        HouseholdItem item = items.get(position);

        TextView itemPurchaseDate = view.findViewById(R.id.item_date);
        TextView itemDescription = view.findViewById(R.id.item_description);
        TextView itemMake = view.findViewById(R.id.item_make);
        TextView itemEstimatedValue = view.findViewById(R.id.item_value);

        itemPurchaseDate.setText(item.getDateOfPurchase());
        itemDescription.setText(item.getDescription());
        itemMake.setText(item.getMake());
        itemEstimatedValue.setText(item.getEstimatedValue());


        CheckBox checkBox = view.findViewById(R.id.checkbox);

        // Set checked status of checkbox based on the checkedPositions array
        checkBox.setChecked(checkedPositions[position]);

        // Add listener to handle checkbox selection
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedPositions[position] = isChecked;
                // Handle the logic for the checked item here if needed
            }
        });

        return view;
    }
}