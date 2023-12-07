package com.example.myapplication;
public class ChatItem {
    private final int type;
    private Message message;
    private HistoryItem historyItem;

    // Константы для определения типа элемента
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_HISTORY = 2;

    // Конструктор для сообщений
    public ChatItem(Message message) {
        this.type = TYPE_MESSAGE;
        this.message = message;
    }

    // Конструктор для истории
    public ChatItem(HistoryItem historyItem) {
        this.type = TYPE_HISTORY;
        this.historyItem = historyItem;
    }

    public int getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }

    public HistoryItem getHistoryItem() {
        return historyItem;
    }
}
