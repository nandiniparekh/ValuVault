package com.example.cmput301groupproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Adapter for displaying a list of images in a RecyclerView.
 */
public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Uri> imageUris;
    private Context context;
    private OnDeleteClickListener deleteClickListener;
    private int type;

    /**
     * Constructs a PhotoAdapter with a list of URIs representing images and a type.
     *
     * @param imageUris The list of URIs representing images
     * @param type      The type of adapter (1 or 2)
     */
    public PhotoAdapter(List<Uri> imageUris, int type) {
        this.imageUris = imageUris;
        this.type = type;

    }


    /**
     * Creates a new PhotoViewHolder by inflating the item_photo layout.
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PhotoViewHolder that holds the View for each list item
     */
    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    /**
     * Updates the contents of the PhotoViewHolder to reflect the item at the given position.
     *
     * @param holder   The PhotoViewHolder to be updated
     * @param position The position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Uri imageUri = imageUris.get(position);
        // Load and display the image directly into the ImageView
        if (type == 1) {
            holder.imageView.setImageURI(imageUri);
        }
        if (type == 2) {
            Picasso.get()
                    .load(imageUri)
                    .into(holder.imageView);
        }

        holder.delete.setOnClickListener(view -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, imageUris.size());
            }
        });
    }


    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items
     */
    @Override
    public int getItemCount() {
        return imageUris.size();
    }
    /**
     * Interface definition for a callback to be invoked when a photo is deleted.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    /**
     * Sets a listener for delete button clicks.
     *
     * @param listener The OnDeleteClickListener instance
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }


    /**
     * ViewHolder for the RecyclerView items displaying photos.
     */
    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, delete;

        /**
         * Constructs a PhotoViewHolder by finding views in the item_photo layout.
         *
         * @param itemView The View object for the RecyclerView item
         */
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}