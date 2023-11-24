package com.example.cmput301groupproject.adapters;

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

import com.example.cmput301groupproject.R;

import java.util.ArrayList;

public class TagsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private ArrayList<String> tags;

    private onTagCheckedChangeListener onTagCheckedChangeListener;

    public interface onTagCheckedChangeListener {
        void onTagCheckedChange(int position, boolean isChecked);
    }

    public void onTagCheckedChangeListener(onTagCheckedChangeListener listener) {
        this.onTagCheckedChangeListener = listener;
    }

    public TagsAdapter(Context context, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;
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

        // Add listener to handle checkbox selection
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onTagCheckedChangeListener != null) {
                    onTagCheckedChangeListener.onTagCheckedChange(position, isChecked);
                }
            }
        });

        return view;
    }
}
