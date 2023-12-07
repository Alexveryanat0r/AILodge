package com.example.myapplication.lama;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DynamicPrompt_ implements Prompt {
    List<StaticPrompt> prompts;

    public DynamicPrompt_() {
        prompts = new ArrayList<>();
    }

    public void addPrompt(StaticPrompt prompt) {
        prompts.add(prompt);
    }

    public void deletePrompt(int index) {
        prompts.remove(index);
    }

    @NonNull
    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < prompts.size(); i++) {
            result += prompts.toString() + " ";
        }
        result = result.substring(0, result.length() - 1);

        return result;
    }
}
