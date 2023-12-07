package com.example.myapplication.lama;

import java.util.ArrayList;
import java.util.List;

//public class RequestPost {
//    List<Message> messages;
//    float temperature;
//    int max_tokens;
//    boolean stream;
//    String prompt;
//
//
//    public RequestPost(float temperature, int max_tokens, boolean stream, String prompt) {
//        List<Message> bf = new ArrayList<>();
//        bf.add(new Message("system", "Always answer in rhymes."));
//        bf.add(new Message("user", "Introduce yourself."));
//        this.messages = bf;
//
//        this.temperature = temperature;
//        this.max_tokens = max_tokens;
//        this.stream = stream;
//        this.prompt = prompt;
//    }
//
//    public float getTemperature() {
//        return temperature;
//    }
//
//    public void setTemperature(float temperature) {
//        this.temperature = temperature;
//    }
//
//    public int getMax_tokens() {
//        return max_tokens;
//    }
//
//    public void setMax_tokens(int max_tokens) {
//        this.max_tokens = max_tokens;
//    }
//
//    public boolean isStream() {
//        return stream;
//    }
//
//    public void setStream(boolean stream) {
//        this.stream = stream;
//    }
//
//    public List<Message> getMessage() {
//        return messages;
//    }
//
//    public void setMessage(List<Message> message) {
//        this.messages = message;
//    }
//
//    class Message {
//        String content, role;
//
//        public Message(String role, String content) {
//            this.content = content;
//            this.role = role;
//        }
//
//        public String getRole() {
//            return role;
//        }
//
//        public void setRole(String role) {
//            this.role = role;
//        }
//
//        public String getContent() {
//            return content;
//        }
//
//        public void setContent(String content) {
//            this.content = content;
//        }
//    }
//}


public class RequestPost {
    List<MessageClass> messages;
    float temperature;
    int max_tokens;
    boolean stream;

    public RequestPost(float temperature, int max_tokens, boolean stream, Prompt prompt, String message) {
        List<MessageClass> bf = new ArrayList<>();
        bf.add(new MessageClass("system", prompt.toString()));
        bf.add(new MessageClass("user", message));
        this.messages = bf;

        this.temperature = temperature;
        this.max_tokens = max_tokens;
        this.stream = stream;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public boolean isStream() {
        return stream;
    }

    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public List<MessageClass> getMessage() {
        return messages;
    }

    public void setMessage(List<MessageClass> message) {
        this.messages = message;
    }
}