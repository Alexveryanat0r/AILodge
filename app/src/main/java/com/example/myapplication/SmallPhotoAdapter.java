package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SmallPhotoAdapter extends RecyclerView.Adapter<SmallPhotoAdapter.ViewHolder> {

    private final List<Integer> photoList;
    private final OnItemClickListener listener;

    public SmallPhotoAdapter(List<Integer> photoList, OnItemClickListener listener) {
        this.photoList = photoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_small_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int photoResId = photoList.get(position);
        holder.smallPhotoImageView.setImageResource(photoResId);

        // Add a click listener to the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the adapter position using getAdapterPosition
                int adapterPosition = holder.getAdapterPosition();

                // Check if the position is valid
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    // Use the adapter position instead of the original position
                    if (listener != null) {
                        listener.onItemClick(adapterPosition);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView smallPhotoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            smallPhotoImageView = itemView.findViewById(R.id.smallPhotoImageView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
