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

public class TagsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> tags;
    private final List<Boolean> checkedStates;
    private onTagCheckedChangeListener onTagCheckedChangeListener;
    private OnCheckboxCheckedChangeListener onCheckboxCheckedChangeListener;

    public interface onTagCheckedChangeListener {
        void onTagCheckedChange(int position, boolean isChecked);
    }
    public void onTagCheckedChangeListener(onTagCheckedChangeListener listener) {
        this.onTagCheckedChangeListener = listener;
    }

    public interface OnCheckboxCheckedChangeListener {
        void onCheckboxChange(int position, boolean isChecked);
    }
    public void setOnCheckboxCheckedChangeListener(OnCheckboxCheckedChangeListener listener) {
        this.onCheckboxCheckedChangeListener = listener;
    }

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

    // Method to clear all checked items
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
