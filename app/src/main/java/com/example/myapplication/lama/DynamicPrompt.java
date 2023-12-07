package com.example.myapplication.lama;

import java.util.List;

public class DynamicPrompt extends Prompt {
    List<StaticPrompt> prompts;
    public DynamicPrompt(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
