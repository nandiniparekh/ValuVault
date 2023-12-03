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

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Uri> imageUris;
    private Context context;
    private OnDeleteClickListener deleteClickListener;
    private int type;

    public PhotoAdapter(List<Uri> imageUris, int type) {
        this.imageUris = imageUris;
        this.type = type;

    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

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
    @Override
    public int getItemCount() {
        return imageUris.size();
    }
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, delete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}