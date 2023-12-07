package com.example.myapplication;

public class Message {
    private String key;
    private String senderId;
    private String text;
    private long timestamp;
    public boolean isHistory; // Добавим это поле
    private String url;
    public Message(){}

    public Message(String senderId, String text, long timestamp, boolean isHistory, String url) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.isHistory = isHistory;
        this.url = url;
    }

    // Getter and setter for the key field
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public boolean getisHistory() {
        return isHistory;
    }
    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }
}
