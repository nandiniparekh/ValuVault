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

/**
 * SelectedItemAdapter class is an ArrayAdapter for displaying a list of selected HouseholdItem
 * objects in a custom layout with checkboxes (thus differing from CustomItemList). It extends
 * ArrayAdapter and overrides the getView method to provide a customized view for each selected item.
 */
public class SelectedItemAdapter extends ArrayAdapter<HouseholdItem> {
    private final Context context;
    private ArrayList<HouseholdItem> items;
    private OnItemCheckedChangeListener onItemCheckedChangeListener;

    /**
     * Interface definition for a callback to be invoked when the checked state of an item changes.
     */
    public interface OnItemCheckedChangeListener {
        /**
         * Called when the checked state of an item changes.
         *
         * @param position The position of the item whose checked state has changed.
         * @param isChecked The new checked state of the item.
         */
        void onItemCheckedChange(int position, boolean isChecked);
    }

    /**
     * Sets the listener for item checked state changes.
     *
     * @param listener The callback to be invoked when the checked state of an item changes.
     */
    public void setOnItemCheckedChangeListener(OnItemCheckedChangeListener listener) {
        this.onItemCheckedChangeListener = listener;
    }

    /**
     * Constructs a new SelectedItemAdapter.
     *
     * @param context The context in which the SelectedItemAdapter is created.
     * @param items   The list of selected HouseholdItem objects to be displayed.
     */
    public SelectedItemAdapter(Context context, ArrayList<HouseholdItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    /**
     * Overrides the getView method to provide a custom view for each selected item.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return The custom view for the selected item at the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.selected_item_display_content, parent, false);
        }

        HouseholdItem item = items.get(position);

        // Fill textviews with minimally required data
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