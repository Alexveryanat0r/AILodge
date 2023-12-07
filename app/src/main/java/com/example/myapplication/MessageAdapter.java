package com.example.myapplication;

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

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE = 1;
    private static final int VIEW_TYPE_HISTORY = 2;

    private final List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener listener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_MESSAGE:
                View messageView = inflater.inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(messageView);
            case VIEW_TYPE_HISTORY:
                View historyView = inflater.inflate(R.layout.item_history, parent, false);
                return new HistoryViewHolder(historyView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE:
                MessageViewHolder messageViewHolder = (MessageViewHolder) viewHolder;
                messageViewHolder.bind(messageList.get(position));
                break;
            case VIEW_TYPE_HISTORY:
                HistoryViewHolder historyViewHolder = (HistoryViewHolder) viewHolder;
                historyViewHolder.bind(messageList.get(position));
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getisHistory() ? VIEW_TYPE_HISTORY : VIEW_TYPE_MESSAGE;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;

        MessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });
        }

        void bind(Message message) {
            textViewMessage.setText(message.getText());
        }
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewHistory;
        private final ImageView imageViewHistory; // Используйте ImageView вместо Image
        HistoryViewHolder(View itemView) {
            super(itemView);
            textViewHistory = itemView.findViewById(R.id.textViewHistory);
            imageViewHistory = itemView.findViewById(R.id.historyImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(view, position);
                        }
                    }
                }
            });
        }

        void bind(Message message) {
            textViewHistory.setText(message.getText());

            // Загрузка изображения с использованием Glide
            Glide.with(itemView.getContext())
                    .load(message.getUrl())
                    .into(imageViewHistory);
        }
    }
}
