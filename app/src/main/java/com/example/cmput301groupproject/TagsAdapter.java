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
import java.util.List;

/**
 * Custom ArrayAdapter for managing and displaying tags with checkboxes.
 */
public class TagsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> tags;
    private final List<Boolean> checkedStates;
    private onTagCheckedChangeListener onTagCheckedChangeListener;
    private OnCheckboxCheckedChangeListener onCheckboxCheckedChangeListener;

    /**
     * Interface for handling tag checked change events.
     */
    public interface onTagCheckedChangeListener {
        void onTagCheckedChange(int position, boolean isChecked);
    }

    /**
     * Sets the listener for tag checked change events.
     *
     * @param listener The listener to be set.
     */
    public void onTagCheckedChangeListener(onTagCheckedChangeListener listener) {
        this.onTagCheckedChangeListener = listener;
    }

    /**
     * Interface for handling checkbox change events.
     */
    public interface OnCheckboxCheckedChangeListener {
        void onCheckboxChange(int position, boolean isChecked);
    }

    /**
     * Sets the listener for checkbox change events.
     *
     * @param listener The listener to be set.
     */
    public void setOnCheckboxCheckedChangeListener(OnCheckboxCheckedChangeListener listener) {
        this.onCheckboxCheckedChangeListener = listener;
    }

    /**
     * Constructs a TagsAdapter.
     *
     * @param context The context in which the adapter is created.
     * @param tags    The list of tags to be displayed.
     */
    public TagsAdapter(Context context, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;

        // Initialize the checked state list
        this.checkedStates = new ArrayList<>();
        for (int i = 0; i < tags.size(); i++) {
            checkedStates.add(false);
        }
    }

    /**
     * Gets the view for each item in the adapter.
     *
     * @param position    The position of the item within the adapter.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view that the returned view will be attached to.
     * @return The view for the specified position.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tag_display, parent, false);
        }

        String tag = tags.get(position);

        TextView tagName = view.findViewById(R.id.textViewTag);
        tagName.setText(tag);

        CheckBox checkBox = view.findViewById(R.id.checkBoxTag);

        // Adjust the size of checkedStates if needed
        while (checkedStates.size() <= position) {
            checkedStates.add(false);
        }

        checkBox.setChecked(checkedStates.get(position));

        // Add listener to handle checkbox selection
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onTagCheckedChangeListener != null) {
                    onTagCheckedChangeListener.onTagCheckedChange(position, isChecked);

                    // Update the checked state in the list
                    checkedStates.set(position, isChecked);
                }
                if (onCheckboxCheckedChangeListener != null) {
                    onCheckboxCheckedChangeListener.onCheckboxChange(position, isChecked);
                }
            }
        });

        return view;
    }

    /**
     * Clears the checked state for all items in the adapter.
     */
    public void clearAllCheckedItems() {
        // Adjust the size of checkedStates if there are more items than tags
        while (checkedStates.size() > tags.size()) {
            checkedStates.remove(checkedStates.size() - 1);
        }

        // Iterate through the positions and update the state of checkboxes
        while (checkedStates.size() > tags.size()) {
            checkedStates.remove(checkedStates.size() - 1);
        }

        for (int i = 0; i < checkedStates.size(); i++) {
            if (onTagCheckedChangeListener != null) {
                onTagCheckedChangeListener.onTagCheckedChange(i, false);

                // Update the checked state in the list
                checkedStates.set(i, false);
            }
        }

        notifyDataSetChanged();
    }
}
