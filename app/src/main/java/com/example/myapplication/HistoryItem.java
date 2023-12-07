package com.example.myapplication;

public class HistoryItem {
    private String text;
    private String imageUrl;
    private  String uid;
    private long timestamp;

    public HistoryItem() {
        // Required for Firebase data mapping
    }

    public HistoryItem(String text, String imageUrl, String uid) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getuid(){
        return uid;
    }
    public void setuid(String uid ){
        this.uid = uid;
    }
}
