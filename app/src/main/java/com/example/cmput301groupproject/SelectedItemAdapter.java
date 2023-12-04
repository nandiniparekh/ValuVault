package com.example.cmput301groupproject;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class SelectedItemAdapter extends ArrayAdapter<HouseholdItem> {
    private final Context context;
    private ArrayList<HouseholdItem> items;
    private OnItemCheckedChangeListener onItemCheckedChangeListener;

    public interface OnItemCheckedChangeListener {
        void onItemCheckedChange(int position, boolean isChecked);
    }

    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.onItemCheckedChangeListener = listener;
    }

    public SelectedItemAdapter(Context context, ArrayList<HouseholdItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
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
        itemEstimatedValue.setText("$" + item.getEstimatedValue());


        CheckBox checkBox = view.findViewById(R.id.checkbox);

        // Add listener to handle checkbox selection
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onItemCheckedChangeListener != null) {
                    onItemCheckedChangeListener.onItemCheckedChange(position, isChecked);
                }
            }
        });

        // Find the LinearLayout for tags
        LinearLayout tagsLayout = view.findViewById(R.id.display_item_tags_layout);

        // Clear existing views in the layout
        tagsLayout.removeAllViews();

        // Loop through the tags and add TextViews dynamically
        for (String tag : item.getTags()) {
            TextView tagTextView = new TextView(context);
            tagTextView.setText(tag);
            tagTextView.setBackgroundResource(R.drawable.tag_background);
            tagTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            tagTextView.setPadding(8, 4, 8, 4);

            // Add the TextView to the LinearLayout
            tagsLayout.addView(tagTextView);
        }

        return view;
    }
}