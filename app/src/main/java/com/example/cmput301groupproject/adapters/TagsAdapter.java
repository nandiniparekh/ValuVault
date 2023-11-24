package com.example.cmput301groupproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmput301groupproject.R;

import java.util.ArrayList;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder> {

    private ArrayList<String> tags;
    private ArrayList<String> selectedTags;

    public TagsAdapter(ArrayList<String> tags) {
        this.tags = tags;
        this.selectedTags = new ArrayList<>();
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }

    public ArrayList<String> getSelectedTags() {
        return selectedTags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tag = tags.get(position);
        holder.bind(tag);

        // Set the checkbox state based on the selectedTags list
        holder.checkBox.setChecked(selectedTags.contains(tag));

        // Set a click listener to handle tag selection
        holder.itemView.setOnClickListener(v -> {
            if (selectedTags.contains(tag)) {
                selectedTags.remove(tag);
            } else {
                selectedTags.add(tag);
            }
            notifyDataSetChanged(); // Update the view to reflect changes
        });
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewTag);
            checkBox = itemView.findViewById(R.id.checkBoxTag);
        }

        public void bind(String tag) {
            textView.setText(tag);
        }
    }
}
