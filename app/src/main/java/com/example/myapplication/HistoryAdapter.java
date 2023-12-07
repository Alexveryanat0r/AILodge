package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryItem> historyList;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public HistoryAdapter() {
        this.historyList = new ArrayList<>();
    }

    public void setHistoryList(List<HistoryItem> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return historyList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(historyList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewHistory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewHistory);
            imageViewHistory = itemView.findViewById(R.id.historyImageView);

            // Устанавливаем обработчик клика прямо в ViewHolder
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        // Вызываем onItemClick с объектом HistoryItem при клике
                        onItemClickListener.onItemClick(historyList.get(position));

                        // Получаем UID выбранного элемента
                        String clickedUid = historyList.get(position).getuid();

                        // Открываем новую активность и передаем UID
                        openHistoryDetailActivity(clickedUid);
                    }
                }
            });
        }

        void bind(HistoryItem historyItem) {
            textViewName.setText(historyItem.getText());

            Glide.with(itemView.getContext())
                    .load(historyItem.getImageUrl())
                    .into(imageViewHistory);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(HistoryItem historyItem);
    }

    public HistoryAdapter(List<HistoryItem> historyList) {
        this.historyList = historyList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void openHistoryDetailActivity(String uid) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }
}