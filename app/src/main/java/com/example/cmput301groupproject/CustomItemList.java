package com.example.cmput301groupproject;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CustomItemList extends ArrayAdapter<HouseholdItem> {
    private final Context context;
    private ArrayList<HouseholdItem> items;
    private String userCollectionPath;

    public CustomItemList(Context context, ArrayList<HouseholdItem> items, String userCollectionPath) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
        this.userCollectionPath = userCollectionPath;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_display_content, parent, false);
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click here
                // You can start a new activity or perform any action you need
                Intent intent = new Intent(context, ItemEditActivity.class);
                intent.putExtra("userDoc", userCollectionPath);
                intent.putExtra("selectedItem", item);
                context.startActivity(intent);
            }
        });

        return view;
    }
}