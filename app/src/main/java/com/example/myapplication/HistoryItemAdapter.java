package com.example.myapplication;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder> {

    private final List<HistoryItem> historyItemList;
    private OnItemClickListener onItemClickListener;
    private int selectedPosition = -1;

    public HistoryItemAdapter(List<HistoryItem> historyItemList) {
        this.historyItemList = historyItemList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public HistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItemViewHolder holder, int position) {
        HistoryItem historyItem = historyItemList.get(position);
        holder.bind(historyItem);

        // Set the background color for the selected item
        holder.itemView.setBackgroundColor(selectedPosition == position ? holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray) : 0);
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, View.OnClickListener, View.OnLongClickListener {

        private final ImageView imageViewHistoryItem;
        private final TextView textViewHistoryItemTitle;

        public HistoryItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewHistoryItem = itemView.findViewById(R.id.historyImageView);
            textViewHistoryItemTitle = itemView.findViewById(R.id.historyImageView);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an action");
            menu.add(Menu.NONE, 1, Menu.NONE, "Edit");
            menu.add(Menu.NONE, 2, Menu.NONE, "Delete");
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                // Handle long click, for example, show edit UI
                initiateEditHistoryItem(position);
            }
            return true;
        }

        private void initiateEditHistoryItem(int position) {
            // Get the itemhistory at the clicked position
            HistoryItem historyItemToEdit = historyItemList.get(position);

            // Change the UI to support editing (e.g., show an EditText)
            // You can create an EditText dynamically and add it to the layout
        }

        public void bind(HistoryItem historyItem) {
            textViewHistoryItemTitle.setText(historyItem.getText());

            // Load image using Glide library
            RequestOptions requestOptions = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(16));
            Glide.with(itemView.getContext())
                    .load(historyItem.getImageUrl())
                    .apply(requestOptions)
                    .into(imageViewHistoryItem);
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
}
