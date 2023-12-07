package com.example.myapplication.lama;

import androidx.annotation.NonNull;

public class StaticPrompt implements Prompt {
    private String value;

    public StaticPrompt(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return value;
    }
}
