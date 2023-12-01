package com.example.cmput301groupproject;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Uri> imageUris;
    public PhotoAdapter(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @Override
    public PhotoAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.PhotoViewHolder holder, @SuppressLint("RecyclerView") int position) {
        //holder.imageView.setImageURI(imageUris.get(position));
        Uri imageUri = imageUris.get(position);
        Glide.with(holder.imageView.getContext())
                .load(imageUri)
                .into(holder.imageView);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUris.remove(imageUri);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
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

